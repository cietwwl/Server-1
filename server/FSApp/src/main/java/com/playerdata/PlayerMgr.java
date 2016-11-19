package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.playerFilter.PlayerFilter;
import com.common.playerFilter.PlayerFilterCondition;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataDeletedException;
import com.rw.fsutil.dao.cache.DataKVCache;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rw.fsutil.dao.optimize.SimpleLoader;
import com.rw.manager.GameManager;
import com.rw.manager.GamePlayerOpHelper;
import com.rw.manager.PlayerCallBackTask;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

/**
 * 玩家管理类
 *
 * 
 * @author Allen
 * @Modified-by Allen
 * @version
 * 
 */

public class PlayerMgr {

	private static PlayerMgr instance = new PlayerMgr();

	private static GamePlayerOpHelper gamePlayerOpHelper = new GamePlayerOpHelper(20);

	private static GamePlayerOpHelper gamePlayerEmailHelper = new GamePlayerOpHelper(20);

	private HashMap<String, String> robotIdMap;

	public static PlayerMgr getInstance() {
		return instance;
	}

	private DataKVCache<String, Player> cache;

	public PlayerMgr() {
		int cacheSize = GameManager.getPerformanceConfig().getPlayerCapacity();
		cache = DataCacheFactory.createDataKVCache(Player.class, cacheSize, 60, loader);
	}

	private SimpleLoader<String, Player> loader = new SimpleLoader<String, Player>() {

		@Override
		public Player load(String key) throws DataNotExistException, Exception {
			return new Player(key, true);
		}

		@Override
		public boolean hasChanged(String key, Player value) {
			return false;
		}

	};

	public Map<String, Player> getAllPlayer() {
		return cache.entries();
	}

	public synchronized void initRobotCache() {
		if (this.robotIdMap != null) {
			throw new ExceptionInInitializerError("duplicate init Player robot id cache");
		}
		List<String> list = UserDataDao.getInstance().queryAllRobotId();
		HashMap<String, String> map = new HashMap<String, String>();
		for (String id : list) {
			map.put(id, id);
		}
		this.robotIdMap = map;
	}

	public void putToMap(Player player) {
		try {
			cache.put(player.getUserId(), player);
		} catch (DataDeletedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 只有初次创建用户的时候才调用这个方法
	 * 
	 * 
	 * @param userId
	 * @return
	 */
	public Player newFreshPlayer(String userId, ZoneLoginInfo zoneLoginInfo) {
		Player player = Player.newFresh(userId, zoneLoginInfo);
		try {
			cache.put(userId, player);
		} catch (DataDeletedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return player;
	}

	public Player findPlayerFromMemory(String userId) {
		return cache.getFromMemory(userId);
	}

	public Player find(String userId) {
		try {
			return cache.getOrLoadFromDB(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取Player只读接口(临时解决方案)
	 * 
	 * @param userId
	 * @return
	 */
	public PlayerIF getReadOnlyPlayer(String userId) {
		return find(userId);
	}

	/****
	 * 根据玩家名字拿 到唯一id
	 * 
	 * @param name
	 *            玩家名字
	 * @param isOnLine
	 *            是否不在线的也获取
	 * @return 玩家uid
	 */
	public String getUserIdByName(String name) {

		User userTable = UserDataDao.getInstance().getByUserName(name);
		if (userTable != null) {
			return userTable.getUserId();
		}

		return null;

	}

	public Player findByName(String name) {

		User userTable = UserDataDao.getInstance().getByUserName(name);
		if (userTable != null) {
			return find(userTable.getUserId());
		}

		return null;

	}

	public int getOPProgress() {
		return gamePlayerOpHelper.getProgress();
	}

	private String offReason = "亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。";
	private boolean blnNeedCoolTime = true; // 是否需要设置kickOffCoolTime
	private final PlayerCallBackTask kickOffTask = new PlayerCallBackTask() {
		public void doCallBack(Player player) {
			player.KickOffWithCoolTime(offReason.toString(), blnNeedCoolTime);
		}

		@Override
		public String getName() {
			return "kickOffTask";
		}
	};

	public int gmKickOffAllPlayer(String reason, boolean _blnNeedCoolTime) {
		offReason = reason;
		blnNeedCoolTime = _blnNeedCoolTime;

		List<Player> playerList = new ArrayList<Player>();
		List<String> onlineList = UserChannelMgr.getOnlineList();
		for (String userId : onlineList) {
			Player target = PlayerMgr.getInstance().findPlayerFromMemory(userId);
			if (target != null) {
				playerList.add(target);
			}
		}
		return gamePlayerOpHelper.addTask(playerList, kickOffTask);
	}

	private final PlayerCallBackTask minuteFuncTask = new PlayerCallBackTask() {
		public void doCallBack(Player player) {
			player.onMinutes();
		}

		@Override
		public String getName() {
			return "minuteFuncTask";
		}
	};

	public int minutesFunc4AllPlayer() {
		List<Player> playerList = getOnlinePlayers();
		return gamePlayerOpHelper.addTask(playerList, minuteFuncTask);
	}

	private final PlayerCallBackTask hourFuncTask = new PlayerCallBackTask() {
		@Override
		public void doCallBack(Player player) {
			player.onNewHour();
		}

		@Override
		public String getName() {
			return "hourFuncTask";
		}
	};

	public int hourFunc4AllPlayer() {
		// List<Player> playerList = new
		// ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = getOnlinePlayers();
		return gamePlayerOpHelper.addTask(playerList, hourFuncTask);
	}

	private final PlayerCallBackTask day5pmFuncTask = new PlayerCallBackTask() {
		@Override
		public void doCallBack(Player player) {
			player.onNewDay5Clock();
		}

		@Override
		public String getName() {
			return "day5pmFuncTask";
		}
	};

	public int day5amFunc4AllPlayer() {
		// List<Player> playerList = new
		// ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = getOnlinePlayers();
		return gamePlayerOpHelper.addTask(playerList, day5pmFuncTask);
	}

	private final PlayerCallBackTask dayZero4FuncTask = new PlayerCallBackTask() {
		@Override
		public void doCallBack(Player player) {
			player.onNewDayZero();
		}

		@Override
		public String getName() {
			return "dayZero4FuncTask";
		}
	};

	public int dayZero4Func4AllPlayer() {
		// List<Player> playerList = new
		// ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = getOnlinePlayers();
		return gamePlayerOpHelper.addTask(playerList, dayZero4FuncTask);
	}

	public int getEmailSendProgress() {
		return gamePlayerEmailHelper.getProgress();
	}

	public int sendEmailToList(List<Player> playerList, final EmailData emailData, final List<PlayerFilterCondition> conditionList) {
		for (Player player : playerList) {
			boolean filted = false;
			for (PlayerFilterCondition conTmp : conditionList) {
				if (!PlayerFilter.isInRange(player, conTmp)) {
					filted = true;
					break;
				}
			}
			long taskId = emailData.getTaskId();
			if (!filted && !player.getEmailMgr().containsEmailWithTaskId(taskId)) {
				EmailUtils.sendEmail(player.getUserId(), emailData);
			}
		}
		return 1;

	}

	public void callbackEmailToList(List<Player> playerList, final EmailData emailData) {

		for (Player player : playerList) {
			GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {

				@Override
				public void run(Player e) {
					long taskId = emailData.getTaskId();
					EmailUtils.deleteEmail(e.getUserId(), taskId);
				}
			});
		}

	}

	public void sendEmailToAll(final EmailData emailData, final List<PlayerFilterCondition> conditionList) {
		List<Player> onlinePlayers = PlayerMgr.getInstance().getOnlinePlayers();
		for (Player player : onlinePlayers) {
			GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {

				@Override
				public void run(Player e) {
					// TODO Auto-generated method stub
					List<Player> temp = new ArrayList<Player>();
					temp.add(e);
					sendEmailToList(temp, emailData, conditionList);
				}
			});
		}

	}

	public void callbackEmail(final EmailData emailData) {

		List<Player> onlinePlayers = PlayerMgr.getInstance().getOnlinePlayers();
		callbackEmailToList(onlinePlayers, emailData);
	}

	/****
	 * 根据玩家等级得到玩家的基本信息 只能拿到>=level的信息
	 * 
	 * @param level
	 *            玩家等级
	 * @return 玩家 UserTable TableUserOther 才有值
	 */
	public List<Player> getAllUserBaseInfoByLevel(int level) {

		List<Player> targetList = new ArrayList<Player>();
		List<User> allUser = UserDataDao.getInstance().getAllUserTable();
		for (User userTmp : allUser) {
			if (userTmp.getLevel() > level) {
				Player playerTmp = find(userTmp.getUserId());
				if (playerTmp != null) {
					targetList.add(playerTmp);
				}
			}
		}
		return targetList;
	}

	/**
	 * 是否在线
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isOnline(String userId) {
		return UserChannelMgr.isConnecting(userId);
	}

	private static PlayerCallBackTask timeSecondTask = new PlayerCallBackTask() {

		@Override
		public String getName() {
			return "secondTimeTask";
		}

		@Override
		public void doCallBack(Player player) {
			player.onSecond();
		}
	};

	/**
	 * 秒时效
	 * 
	 * @return
	 */
	public int secondFunc4AllPlayer() {
		List<Player> playerList = getOnlinePlayers();
		return gamePlayerOpHelper.addTask(playerList, timeSecondTask);
	}

	public List<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<Player>();
		for (String s : UserChannelMgr.getOnlinePlayerIdSet()) {
			Player p = find(s);
			if (p != null) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * <pre>
	 * 执行在线玩家读相关操作
	 * 用于代替{@link #getOnlinePlayers()}
	 * 优化对{@link Player}对象的内存管理
	 * </pre>
	 * @param action
	 */
	public void execteOnlineOperation(DataValueAction<Player> action) {
		cache.rangeRead(UserChannelMgr.getOnlinePlayerIdSet(), action);
	}

	public void setRedPointForHeartBeat(String userId) {
		if (isOnline(userId)) {
			Player otherPlayer = findPlayerFromMemory(userId);
			if (otherPlayer != null) {
				otherPlayer.getTempAttribute().setRedPointChanged();
			}
		}
	}

	/**
	 * <pre>
	 * 通过角色id判断是否机器人
	 * 此方法只对生成在数据库的机器人有效
	 * </pre>
	 * @param userId
	 * @return
	 */
	public boolean isPersistantRobot(String userId) {
		return robotIdMap.containsKey(userId);
	}

	public String getPersistantRobotId(String userId) {
		return this.robotIdMap.get(userId);
	}
}