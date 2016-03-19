package com.rw.platform;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bm.login.ZoneBM;
import com.rw.account.Account;
import com.rw.account.ZoneInfoCache;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.log.EngineLogger;
import com.rw.netty.UserChannelMgr;
import com.rw.netty.client.Client;
import com.rw.netty.client.ClientManager;
import com.rw.platform.data.PlatformNoticeDataHolder;
import com.rw.platform.data.ZoneDataHolder;
import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.dao.zone.TableZoneInfoDAO;

public class PlatformService {

	public final static int SERVER_RECOMMAND = 1; // 推荐服
	/**
	 * 所有在线的account
	 */
	private final static ConcurrentHashMap<String, Account> AccountMap = new ConcurrentHashMap<String, Account>();

	/**
	 * 服务器列表信息
	 */
	private final static ConcurrentHashMap<Integer, ZoneInfoCache> ZoneMap = new ConcurrentHashMap<Integer, ZoneInfoCache>();
	/**
	 * 推荐的服务器
	 */
	private final static ArrayList<ZoneInfoCache> RecommandZoneMap = new ArrayList<ZoneInfoCache>();
	
	private static List<TableZoneInfo> allZoneList = new ArrayList<TableZoneInfo>();

	private final EngineLogger logger; // 平台专用logger
	private final ThreadPoolExecutor executor; // 平台的线程池

	private PlatformNoticeDataHolder platformNoticeDataHolder;
	private ZoneDataHolder zoneDataHolder;

	private ScheduledThreadPoolExecutor checkExecutor;

	public void init() {
		platformNoticeDataHolder = new PlatformNoticeDataHolder();
		zoneDataHolder = new ZoneDataHolder();
		initZoneCache();

		checkExecutor = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("platform"));
		checkExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					refresh();
					
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}, 1, 30, TimeUnit.SECONDS);

	}

	public PlatformService(int threadSize, EngineLogger logger) {
		this.logger = logger;
		this.executor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		init();
	}

	public void refresh() {
		refreshZoneStatus();
		initZoneCache();
	}

	public void initZoneCache() {
		allZoneList = zoneDataHolder.getZoneList();
		RecommandZoneMap.clear();
		for (TableZoneInfo tableZoneInfo : allZoneList) {
			ZoneInfoCache zoneInfoCache = ZoneMap.get(tableZoneInfo.getZoneId());
			if(zoneInfoCache == null){
				zoneInfoCache = new ZoneInfoCache(tableZoneInfo);
			}
			ZoneMap.put(tableZoneInfo.getZoneId(), zoneInfoCache);
			if (tableZoneInfo.getRecommand() == PlatformService.SERVER_RECOMMAND) {
				RecommandZoneMap.add(zoneInfoCache);
			}
		}
	}
	
	public void refreshZoneStatus() {
		for (Iterator<Entry<Integer, ZoneInfoCache>> iterator = ZoneMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, ZoneInfoCache> entry = iterator.next();
			ZoneInfoCache zoneInfoCache = entry.getValue();
			zoneInfoCache.checkStatus();
			zoneInfoCache.update();
		}
	}

	public ZoneInfoCache getZoneInfo(int zoneId) {
		ZoneInfoCache zoneInfoCache = ZoneMap.get(zoneId);
		return zoneInfoCache;
	}

	/**
	 * 获取推荐服
	 * 
	 * @return
	 */
	public ZoneInfoCache getLastZoneCfg(boolean isWhite) {
		ZoneInfoCache zoneCache = null;
		if (RecommandZoneMap.size() > 0) {
			for (ZoneInfoCache zoneInfoCache : RecommandZoneMap) {
				if(zoneInfoCache.getEnabled() == 0 && !isWhite){
					continue;
				}
				if(zoneCache == null){
					zoneCache = zoneInfoCache;
					continue;
				}
				if((!zoneCache.getIsOpen(zoneCache.getStatus()) && zoneInfoCache.getIsOpen(zoneCache.getStatus())) || (zoneInfoCache.getZoneId() > zoneCache.getZoneId() && zoneInfoCache.getIsOpen(zoneCache.getStatus()))){
					zoneCache = zoneInfoCache;
				}
			}
			
		} else {
			for (Iterator<Entry<Integer, ZoneInfoCache>> iterator = ZoneMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, ZoneInfoCache> entry = iterator.next();
				ZoneInfoCache zoneInfoCache = entry.getValue();
				if(zoneInfoCache.getEnabled() == 0){
					continue;
				}
				if(zoneCache == null){
					zoneCache = zoneInfoCache;
					continue;
				}
				if((!zoneCache.getIsOpen(zoneCache.getStatus()) && zoneInfoCache.getIsOpen(zoneCache.getStatus())) || (zoneInfoCache.getZoneId() > zoneCache.getZoneId() && zoneInfoCache.getIsOpen(zoneCache.getStatus()))){
					zoneCache = zoneInfoCache;
				}
			}
		}
		return zoneCache;
	}

	public List<ZoneInfoCache> getAllZoneList() {
		List<ZoneInfoCache> list = new ArrayList<ZoneInfoCache>();
		list.addAll(ZoneMap.values());
		Collections.sort(list, ZoneComparator);
		return list;
	}

	/**
	 * zone排序器，升序
	 */
	private Comparator<ZoneInfoCache> ZoneComparator = new Comparator<ZoneInfoCache>() {

		public int compare(ZoneInfoCache o1, ZoneInfoCache o2) {
			// TODO Auto-generated method stub
			if (o1.getZoneId() > o2.getZoneId()) {
				return 1;
			} else {
				return -1;
			}
		}
	};

	public void submitClientTask(Client client) {
		executor.execute(client);
	}

	public void addAccount(Account account) {
		AccountMap.put(account.getAccountId(), account);
	}

	public Account getAccount(String accountId) {
		return AccountMap.get(accountId);
	}

	public void removeAccount(ChannelHandlerContext ctx) {
		String accountId = UserChannelMgr.getUserId(ctx);
		if (accountId != null) {
			AccountMap.remove(accountId);
			PlatformFactory.clientManager.removeUnSendMsgWhenDisconnect(accountId);

			// 迟点加缓存处理
			UserChannelMgr.remove(ctx);
		}
	}
	
	public TablePlatformNotice getPlatformNotice(){
		return platformNoticeDataHolder.getPlatformNotice();
	}
	
	public void updatePlatformNotice(TablePlatformNotice platformNotice, boolean insert){
		platformNoticeDataHolder.updatePlatformNotice(platformNotice, insert);
	}
	
	public void updateZoneInfo(ZoneInfoCache cache){
		for (TableZoneInfo zoneInfo : allZoneList) {
			if(zoneInfo.getZoneId() == cache.getZoneId()){
				zoneInfo.setStatus(cache.getStatus());
				zoneDataHolder.updateZoneInfo(zoneInfo);
				initZoneCache();
				break;
			}
		}
	}
}
