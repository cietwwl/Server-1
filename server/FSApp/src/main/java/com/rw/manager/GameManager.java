package com.rw.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.bm.arena.RobotManager;
import com.bm.login.ZoneBM;
import com.bm.player.ObserverFactory;
import com.bm.rank.ListRankingType;
import com.bm.rank.RankDataMgr;
import com.bm.rank.RankType;
import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.log.GameLog;
import com.playerdata.GlobalDataMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rw.fsutil.util.DateUtils;
import com.rw.netty.UserChannelMgr;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.gm.GMHandler;
import com.rw.service.http.HttpServer;
import com.rw.service.log.LogService;
import com.rw.service.platformService.PlatformInfo;
import com.rw.service.platformService.PlatformService;
import com.rw.service.platformgs.PlatformGSService;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.playerext.PlayerAttrChecker;
import com.rwbase.dao.anglearray.pojo.AngleArrayMatchHelper;
import com.rwbase.dao.gameNotice.pojo.GameNoticeDataHolder;
import com.rwbase.dao.group.GroupCheckDismissTask;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwbase.gameworld.GameWorldFactory;

public class GameManager {

	private static String serverId;
	private static int generateIdNumber;
	private static int generateTotalNumber;
	private static boolean reloadconfig;
	// author:lida 2015-09-23 区id
	private static int zoneId;
	private static int httpPort;
	private static long openTime; // 新服开服时间
	private static List<PlatformInfo> platformInfos = new ArrayList<PlatformInfo>(); //登陆服信息
	private static String logServerIp; // 日志服ip
	private static int logServerPort; // 日志服端口
	private static ServerPerformanceConfig performanceConfig;
	private static GameNoticeDataHolder gameNotice = new GameNoticeDataHolder();

	/**
	 * 初始化所有后台服务
	 */
	public static void initServiceAndCrontab() {

		long timers = System.currentTimeMillis();
		long tempTimers = 0;

		GameLog.debug("初始化后台服务");
		// TODO 游戏逻辑处理线程数，需要在配置里面统一配置
		GameWorldFactory.getGameWorld().registerPlayerDataListener(new PlayerAttrChecker());
		tempTimers = System.currentTimeMillis();

		initServerProperties();

		initSwitchProperties();
		
		initServerPerformanceConfig();
		
		/**** 服务器全启数据 ******/
		GlobalDataMgr.init();
		// 初始化 日志服务初始化
		LogService.getInstance().init();

		// 开服活动初始化
		FresherActivityChecker.init();
		/**** 公会数据 ******/
		// GuildGTSMgr.getInstance().init();
		// // 试练塔攻略加载
		// BattleTowerStrategyManager.init();
		/******* 帮派解散时效数据初始化 *******/
		GroupCheckDismissTask.initDismissGroupInfo();
		/******* 游戏模块数据观察者数据初始化 *******/
		ObserverFactory.getInstance().initFactory();

		/**** 排行初始化 ******/
		// ArenaBM.getInstance().InitData();
		// 初始化排行榜系统
		ScheduledThreadPoolExecutor rankingPool = new ScheduledThreadPoolExecutor(1);
		ScheduledThreadPoolExecutor listRankingPool = new ScheduledThreadPoolExecutor(1);
		RankingFactory.init(Arrays.asList(RankType.values()), Arrays.asList(ListRankingType.values()), rankingPool, listRankingPool);

		tempTimers = System.currentTimeMillis();
		GameLog.debug("竞技场初始化用时:" + (System.currentTimeMillis() - tempTimers) + "毫秒");
		// PeakArenaBM.getInstance().InitData();
		tempTimers = System.currentTimeMillis();
		// RobotBM.getInstance().createArenaUsers(true);
		RobotManager.getInstance().createRobots();
		GameLog.debug("创建竞技场机器人用时:" + (System.currentTimeMillis() - tempTimers) + "毫秒");

		tempTimers = System.currentTimeMillis();
		RankingMgr.getInstance().onInitRankData();
		GameLog.debug("排行排序用时:" + (System.currentTimeMillis() - tempTimers) + "毫秒");
		/**** 游戏时间功能 ******/
		TimerManager.init();

		PlatformService.init();
		// author:lida 2015-09-23 启动游戏服通知平台服务器
		PlatformGSService.init();

		// 初始化字符过滤
		CharFilterFactory.init();
		addShutdownHook();

		// 初始化万仙阵匹配的数据缓存
		long start = System.currentTimeMillis();
		AngleArrayMatchHelper.resetMatchData();
		long end = System.currentTimeMillis();
		System.err.println("万仙阵初始化匹配数据花费时间：" + (end - start) + "毫秒");
		System.err.println("初始化后台完成,共用时:" + (System.currentTimeMillis() - timers) + "毫秒");
		
	}

	private static void initServerProperties() {
		Resource resource = new ClassPathResource("server.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			serverId = props.getProperty("serverId");
			generateIdNumber = Integer.parseInt(props.getProperty("generateIdNumber"));
			generateTotalNumber = Integer.parseInt(props.getProperty("generateTotalNumber"));
			zoneId = Integer.parseInt(props.getProperty("zoneId"));
			httpPort = Integer.parseInt(props.getProperty("httpPort"));
			String strPlatformUrl = props.getProperty("platformUrl");
			String[] split = strPlatformUrl.split(",");
			for (String value : split) {
				String[] subSplit = value.split(":");
				if(subSplit.length >1){
					String ip = subSplit[0];
					int port = Integer.parseInt(subSplit[1]);
					PlatformInfo platformInfo = new PlatformInfo(ip, port);
					platformInfos.add(platformInfo);
				}
			}
			TableZoneInfo zoneInfo = ZoneBM.getInstance().getTableZoneInfo(zoneId);
			openTime = DateUtils.getTime(zoneInfo.getOpenTime());
			logServerIp = props.getProperty("logServerIp");
			logServerPort = Integer.parseInt(props.getProperty("logServerPort"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void initServerPerformanceConfig(){
		Resource rs = new ClassPathResource("serverParam.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(rs);
			int playerCapacity = Integer.parseInt(props.getProperty("playerCapacity"));
			int heroCapacity = Integer.parseInt(props.getProperty("heroCapacity"));
			int itemCapacity = Integer.parseInt(props.getProperty("itemCapacity"));
			ServerPerformanceConfig config = new ServerPerformanceConfig(playerCapacity, heroCapacity, itemCapacity);
			performanceConfig = config;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//使用默认配置
			ServerPerformanceConfig config = new ServerPerformanceConfig(3000, 20000, 50000);
			performanceConfig = config;
		}
	}
	
	private static void initSwitchProperties(){
		Resource resource = new ClassPathResource("switch.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			boolean serverstatus = Boolean.parseBoolean(props.getProperty("serverStatus"));
			if (serverstatus) {
				ServerStatusMgr.setStatus(ServerStatus.OPEN);
			} else {
				ServerStatusMgr.setStatus(ServerStatus.CLOSE);
			}
			boolean gmSwitch = Boolean.parseBoolean(props.getProperty("gmSwitch"));
			GMHandler.getInstance().setActive(gmSwitch);
		} catch (IOException e) {

		}
	}

	/*** 服务器关闭装态 **/
	public static boolean isShutdownHook = false;

	/*** 服务器关闭事件 *******/
	private static void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		}));
	}

	public static void shutdown() {
		isShutdownHook = true;
		GameLog.debug("服务器开始关闭...");
		List<Player> list = new ArrayList<Player>();
		list.addAll(PlayerMgr.getInstance().getAllPlayer().values());
		/**** 保存在线玩家 *******/
		PlayerMgr.getInstance().saveAllPlayer();
		// PlayerMgr.getInstance().kickOffAllPlayer();

		shutDownService();
		ShutdownService.notifyShutdown();
		GameLog.debug("服务器关闭完成...");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void shutDownService() {
		// flush 排名数据
		RankDataMgr.getInstance().flushData();
		ExecutorService executor = Executors.newFixedThreadPool(50);
		ArrayList<NameFuture> allTasks = new ArrayList<NameFuture>();
		for (final DataCache dao : DataCacheFactory.getAllCaches()) {
			final String name = dao.getName();
			List<Runnable> tasks = dao.createUpdateTask();
			int size = tasks.size();
			GameLog.error(name + " 保存数据：" + size);
			final AtomicInteger taskCount = new AtomicInteger(size);
			for (int i = 0; i < size; i++) {
				final Runnable task = tasks.get(i);
				Future t = executor.submit(new Callable() {

					@Override
					public Object call() throws Exception {
						task.run();
						if (taskCount.decrementAndGet() == 0) {
							GameLog.error(name + " 保存数据完毕");
							//System.err.println(name + " 保存数据完毕");
						}
						return name;
					}
				});
				allTasks.add(new NameFuture(t, name));
			}
		}
		boolean interrupted = false;
		if (Thread.interrupted()) {
			interrupted = true;
		}
		int size = allTasks.size();
		GameLog.error("保存数据总量：" + size);
		//System.err.println("保存数据总量：" + size);
		for (int i = 0; i < size; i++) {
			NameFuture nameFuture = allTasks.get(i);
			try {
				nameFuture.future.get();
			} catch (InterruptedException e) {
				GameLog.error("保存数据时接收到inerruptException：" + nameFuture.name);
				interrupted = true;
			} catch (ExecutionException e) {
				GameLog.error("保存数据ExecutionException：" + nameFuture.name + "," + e);
			}
		}
		GameLog.error("停服保存数据完毕：" + size);
		//System.err.println("停服保存数据完毕：" + size);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static class NameFuture {
		private final Future future;
		private final String name;

		public NameFuture(Future future, String name) {
			super();
			this.future = future;
			this.name = name;
		}

	}

	/**
	 * 
	 * @return
	 */
	public static boolean isOnlineLimit() {
		//没有计算当前请求登陆的人，只能手动加1了
		return ServerStatusMgr.getOnlineLimit() < (UserChannelMgr.getCount()+1);
	}

	public static boolean isWhiteListLimit(String accountId) {
		boolean serverClose = ServerStatusMgr.getStatus() == ServerStatus.CLOSE;
		if (serverClose && StringUtils.isBlank(accountId)) {
			return true;
		}
		boolean whiteListON = ServerStatusMgr.isWhilteListON();
		boolean userInWhiteList = ServerStatusMgr.getWhiteList().contains(accountId);
		boolean isInWhiteList = (whiteListON && userInWhiteList);
		return serverClose && !isInWhiteList;
	}

	public static String getServerId() {
		return serverId;
	}

	public static int getGenerateIdNumber() {
		return generateIdNumber;
	}

	public static int getGenerateTotalNumber() {
		return generateTotalNumber;
	}

	public static boolean isReloadconfig() {
		return reloadconfig;
	}

	public static void setReloadconfig(boolean reloadconfig) {
		GameManager.reloadconfig = reloadconfig;
	}

	public static int getZoneId() {
		return zoneId;
	}

	public static int getHttpPort() {
		return httpPort;
	}

	public static long getOpenTime() {
		return openTime;
	}

	public static List<PlatformInfo> getPlatformInfos() {
		return platformInfos;
	}

	public static String getLogServerIp() {
		return logServerIp;
	}

	public static int getLogServerPort() {
		return logServerPort;
	}

	public static GameNoticeDataHolder getGameNotice() {
		return gameNotice;
	}

	public static ServerPerformanceConfig getPerformanceConfig() {
		return performanceConfig;
	}
}
