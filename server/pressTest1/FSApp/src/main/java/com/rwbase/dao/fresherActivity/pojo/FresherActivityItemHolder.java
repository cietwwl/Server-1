package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 开服活动数据
 * 
 * @author lida
 *
 */
public class FresherActivityItemHolder {
	private final static long DAY_TIME = 24 * 60 * 60 * 1000l;
	final private String ownerId;
	// final private MapItemStore<FresherActivityItem> fresherActivityStore;
	// 玩家当前的开服活动对象
	private ConcurrentHashMap<Integer, FresherActivityItem> FresherActivityMap = new ConcurrentHashMap<Integer, FresherActivityItem>();
	private HashMap<eActivityType, List<FresherActivityItem>> FresherActivityMapByType = new HashMap<eActivityType, List<FresherActivityItem>>();
	final private eSynType synType = eSynType.FRESHER_ATIVITY_DATA;
	private boolean blnFinish = true;
	private boolean modified = false;

	private List<FresherActivityItem> notSaveNewList = new ArrayList<FresherActivityItem>();

	/**
	 * 初始化玩家的开服活动数据
	 * 
	 * @param userId
	 */
	public FresherActivityItemHolder(String userId) {
		ownerId = userId;
		// fresherActivityStore = new
		// MapItemStore<FresherActivityItem>("ownerId", ownerId,
		// FresherActivityItem.class);
		getFresherActivityItemList();
		initFresherActivityMap();
	}

	private void getFresherActivityItemList() {

		Enumeration<FresherActivityItem> mapEnum = getMapItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			FresherActivityItem fresherActivityItem = mapEnum.nextElement();
			FresherActivityMap.put(fresherActivityItem.getCfgId(), fresherActivityItem);
		}
	}

	/**
	 * 根据配置获取所有的新手活动配置
	 */
	private void initFresherActivityMap() {
		long start = System.currentTimeMillis();
		long current = System.currentTimeMillis();
		List<FresherActivityCfg> allCfg = FresherActivityCfgDao.getInstance().getAllCfg();
		for (FresherActivityCfg fresherActivityCfg : allCfg) {
			int cfgId = fresherActivityCfg.getCfgId();
			if (FresherActivityMap.containsKey(cfgId)) {
				FresherActivityItem fresherActivityItem = FresherActivityMap.get(cfgId);
				initFresherActivityMapByType(fresherActivityCfg, fresherActivityItem, current);
				refreshActivityTime(fresherActivityItem, fresherActivityCfg);
				continue;
			} else {
				FresherActivityItem fresherActivityItem = new FresherActivityItem();

				refreshActivityTime(fresherActivityItem, fresherActivityCfg);
				long endTime = fresherActivityItem.getEndTime();
				if (endTime != -1 && endTime <= current) {
					continue;
				}
				fresherActivityItem.setCfgId(cfgId);
				fresherActivityItem.setId(ownerId + cfgId);
				fresherActivityItem.setOwnerId(ownerId);
				fresherActivityItem.setType(fresherActivityCfg.geteType());
				String maxValue = fresherActivityCfg.getMaxValue();
				if (maxValue != null && !maxValue.equals("")) {
					fresherActivityItem.setCurrentValue("0/" + fresherActivityCfg.getMaxValue());
				}
				initFresherActivityMapByType(fresherActivityCfg, fresherActivityItem, current);
				FresherActivityMap.put(cfgId, fresherActivityItem);
				notSaveNewList.add(fresherActivityItem);
				modified = true;
			}
		}

		long end = System.currentTimeMillis();

	}

	private void initFresherActivityMapByType(FresherActivityCfg fresherActivityCfg, FresherActivityItem fresherActivityItem, long current) {
		// 有活动可以领奖但是没有领奖 则表示活动还没有结束
		if (!fresherActivityItem.isFinish() || (!fresherActivityItem.isGiftTaken() && fresherActivityItem.isFinish()) || fresherActivityItem.getEndTime() > System.currentTimeMillis()) {
			blnFinish = false;
		}

		if (FresherActivityMapByType.containsKey(fresherActivityCfg.geteType())) {
			List<FresherActivityItem> list = FresherActivityMapByType.get(fresherActivityCfg.geteType());
			list.add(fresherActivityItem);
		} else {
			List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
			list.add(fresherActivityItem);
			FresherActivityMapByType.put(fresherActivityCfg.geteType(), list);
		}
	}

	/**
	 * 读取配置表的时间
	 * 
	 * @param fresherActivityItem
	 * @param fresherActivityCfg
	 */
	private void refreshActivityTime(FresherActivityItem fresherActivityItem, FresherActivityCfg fresherActivityCfg) {
		long openTime = GameManager.getOpenTime();
		fresherActivityItem.setStartTime(fresherActivityCfg.getStartTime() * DAY_TIME + openTime);
		if (fresherActivityCfg.getEndTime() == -1) {
			fresherActivityItem.setEndTime(-1);
		} else {
			fresherActivityItem.setEndTime(fresherActivityCfg.getStartTime() * DAY_TIME + openTime + fresherActivityCfg.getEndTime() * DAY_TIME);
		}
	}

	/**
	 * 返回指定类型的活动
	 * 
	 * @param type
	 * @return
	 */
	public List<FresherActivityItem> getFresherActivityItemsByType(eActivityType type) {
		List<FresherActivityItem> list = FresherActivityMapByType.get(type);
		return list;
	}

	/**
	 * 根据id返回指定活动
	 * 
	 * @param cfgId
	 * @return
	 */
	public FresherActivityItem getFresherActivityItemsById(int cfgId) {
		return FresherActivityMap.get(cfgId);
	}

	public Enumeration<FresherActivityItem> getEnumerationActivity() {
		return FresherActivityMap.elements();
	}

	public void flush() {
		if (modified) {
			modified = false;
			long current = System.currentTimeMillis();
			MapItemStore<FresherActivityItem> fresherActivityStore = getMapItemStore();
			if (notSaveNewList.size() > 0) {
				for (Iterator<FresherActivityItem> iterator = notSaveNewList.iterator(); iterator.hasNext();) {
					fresherActivityStore.addItem(iterator.next());
					iterator.remove();
				}
				notSaveNewList.clear();
			}
			System.out.println("消耗时间:"+(System.currentTimeMillis() - current));
			fresherActivityStore.flush();
		}
	}

	public void synAllData(Player player, int version) {
		if (blnFinish) {
			return;
		}
		flush();
		Collection<FresherActivityItem> values = FresherActivityMap.values();
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>(values);
		synListData(player, list);
	}

	public void synListData(Player player, List<FresherActivityItem> list) {
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}

	public void synData(Player player, FresherActivityItem fresherActivityItem) {
		getMapItemStore().updateItem(fresherActivityItem);
		ClientDataSynMgr.synData(player, fresherActivityItem, synType, eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 更新活动状态
	 * 
	 * @param player
	 * @param result
	 */
	public void completeFresherActivity(Player player, FresherActivityCheckerResult result) {
		List<FresherActivityItem> refreshList = new ArrayList<FresherActivityItem>();
		List<Integer> synCfgId = new ArrayList<Integer>();
		// 同步更新完成状态的活动
		MapItemStore<FresherActivityItem> fresherActivityStore = getMapItemStore();
		for (Integer activityId : result.getCompleteList()) {
			FresherActivityItem fresherActivityItem = FresherActivityMap.get(activityId);
			fresherActivityItem.setFinish(true);
			refreshList.add(fresherActivityItem);
			fresherActivityStore.updateItem(fresherActivityItem);
			synCfgId.add(fresherActivityItem.getCfgId());
		}

		// 同步更新进度的活动
		Map<Integer, String> currentProgress = result.getCurrentProgress();
		for (Iterator<Entry<Integer, String>> iterator = currentProgress.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, String> entry = iterator.next();
			int cfgId = entry.getKey();
			FresherActivityItem fresherActivityItem = FresherActivityMap.get(cfgId);
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			if (fresherActivityCfg.getMaxValue() != null && !fresherActivityCfg.getMaxValue().equals("")) {
				fresherActivityItem.setCurrentValue(entry.getValue() + "/" + fresherActivityCfg.getMaxValue());
			}
			if (!synCfgId.contains(cfgId)) {
				synCfgId.add(cfgId);
				refreshList.add(fresherActivityItem);
				fresherActivityStore.updateItem(fresherActivityItem);
			}
		}
		modified = true;
		synListData(player, refreshList);
	}

	/**
	 * 设置领取活动奖励成功的标志位
	 * 
	 * @param player
	 * @param cfgId
	 */
	public void achieveFresherActivityReward(Player player, int cfgId) {
		FresherActivityItem fresherActivityItem = FresherActivityMap.get(cfgId);
		fresherActivityItem.setGiftTaken(true);
		fresherActivityItem.setClosed(true);
		getMapItemStore().updateItem(fresherActivityItem);
		modified = true;
		synData(player, fresherActivityItem);
	}

	private MapItemStore<FresherActivityItem> getMapItemStore() {
		MapItemStoreCache<FresherActivityItem> cache = MapItemStoreFactory.getFresherActivityCache();
		return cache.getMapItemStore(ownerId, FresherActivityItem.class);
	}

}
