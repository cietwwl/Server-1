package com.playerdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.playerFilter.PlayerFilter;
import com.common.playerFilter.PlayerFilterCondition;
import com.google.protobuf.ByteString;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.cacheDao.CommonUpdateMgr;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataDeletedException;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.common.DBThreadPoolMgr;
import com.rw.manager.GameManager;
import com.rw.manager.GamePlayerOpHelper;
import com.rw.manager.PlayerTask;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;

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

	public static PlayerMgr getInstance() {
		return instance;
	}

	private DataCache<String, Player> cache;

	public PlayerMgr(){
		int cacheSize = GameManager.getPerformanceConfig().getPlayerCapacity();
//		cache = new DataCache<String, Player>("player", cacheSize, cacheSize, 60, DBThreadPoolMgr.getExecutor(), loader,null);
		cache = DataCacheFactory.createDataDache("player", cacheSize, cacheSize, 60, loader);
	}
	
	private PersistentLoader<String, Player> loader = new PersistentLoader<String, Player>() {

		@Override
		public Player load(String key) throws DataNotExistException, Exception {
			return new Player(key, true);
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			//玩家不支持删除
			return false;
		}

		@Override
		public boolean insert(String key, Player value) throws DuplicatedKeyException, Exception {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean updateToDB(String key, Player player) {
			//player.save();
			return true;
		}
	};
	
	public Map<String, Player> getAllPlayer() {
		return cache.entries();
	}
	
	public void putToMap(Player player){
		try {
			cache.put(player.getUserId(), player);
		} catch (DataDeletedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 只有初次创建用户的时候才调用这个方法
	 * 
	 * @param userId
	 * @return
	 */
	public Player newFreshPlayer(String userId) {
		Player player = Player.newFresh(userId);
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
	 * @param name 玩家名字
	 * @param isOnLine 是否不在线的也获取
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

	private final PlayerTask saveTask = new PlayerTask() {
		public void doCallBack(Player player) {
			player.save(true);

		}

		@Override
		public String getName() {
			return "saveTask";
		}
	};

	public int saveAllPlayer() {
		List<Player> playerList = new ArrayList<Player>(getAllPlayer().values());
		int progress = gamePlayerOpHelper.addTask(playerList, saveTask);
		CommonUpdateMgr.getInstance().flushData();
		return progress;
	}

	private String offReason = "亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。";
	private boolean blnNeedCoolTime = true;   //是否需要设置kickOffCoolTime
	private final PlayerTask kickOffTask = new PlayerTask() {
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
		
		//List<Player> playerList = new ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = cache.values();
		return gamePlayerOpHelper.addTask(playerList, kickOffTask);
	}

	private final PlayerTask minuteFuncTask = new PlayerTask() {
		public void doCallBack(Player player) {
			player.onMinutes();
		}

		@Override
		public String getName() {
			return "minuteFuncTask";
		}
	};

	public int minutesFunc4AllPlayer() {
		//List<Player> playerList = new ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = cache.values();
		return gamePlayerOpHelper.addTask(playerList, minuteFuncTask);

	}

	private final PlayerTask hourFuncTask = new PlayerTask() {
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
		//List<Player> playerList = new ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = cache.values();
		return gamePlayerOpHelper.addTask(playerList, hourFuncTask);
	}

	private final PlayerTask day5pmFuncTask = new PlayerTask() {
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
		//List<Player> playerList = new ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = cache.values();
		return gamePlayerOpHelper.addTask(playerList, day5pmFuncTask);
	}

	private final PlayerTask dayZero4FuncTask = new PlayerTask() {
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
		//List<Player> playerList = new ArrayList<Player>(m_PlayerMap.values());
		List<Player> playerList = cache.values();
		return gamePlayerOpHelper.addTask(playerList, dayZero4FuncTask);
	}

	public int getEmailSendProgress() {
		return gamePlayerEmailHelper.getProgress();
	}

	public int sendEmailToList(List<Player> playerList, final EmailData emailData, final List<PlayerFilterCondition> conditionList) {
		PlayerTask playerTask = new PlayerTask() {
			@Override
			public void doCallBack(Player player) {
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

			@Override
			public String getName() {
				return "sendEmailToList";
			}
		};
		return gamePlayerEmailHelper.addTask(playerList, playerTask);

	}
	
	public int callbackEmailToList(List<Player> playerList, final EmailData emailData) {
		PlayerTask playerTask = new PlayerTask() {
			@Override
			public void doCallBack(Player player) {
				long taskId = emailData.getTaskId();
				EmailUtils.deleteEmail(player.getUserId(), taskId);
			}

			@Override
			public String getName() {
				return "sendEmailToList";
			}
		};
		return gamePlayerEmailHelper.addTask(playerList, playerTask);

	}

	public int sendEmailToAll(final EmailData emailData, final List<PlayerFilterCondition> conditionList) {
		List<Player> playerList = new ArrayList<Player>();
		
		List<User> allUserList = UserDataDao.getInstance().queryAll();
		for (User user : allUserList) {
			Player player = find(user.getUserId());
			if(player!=null){
				playerList.add(player);
			}
		}
		return sendEmailToList(playerList, emailData, conditionList);

	}
	
	public int callbackEmail(final EmailData emailData){
		List<Player> playerList = new ArrayList<Player>();
		
		List<User> allUserList = UserDataDao.getInstance().queryAll();
		for (User user : allUserList) {
			Player player = find(user.getUserId());
			if(player!=null){
				playerList.add(player);
			}
		}
		return callbackEmailToList(playerList, emailData);
	}

	/****
	 * 根据玩家等级得到玩家的基本信息 只能拿到>=level的信息
	 * 
	 * @param level 玩家等级
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

	// public void sendToOtherPlayer(MsgDef.Command cmd, ByteString pBuffer, Player me) {
	// try {
	// Set<Entry<String, Player>> players = m_PlayerMap.entrySet();
	// for (Entry<String, Player> entry : players) {
	// Player player = entry.getValue();
	// if (player != null && !player.getUserId().equals(me.getUserId())) {
	// player.SendMsgByOther(cmd, pBuffer);
	// }
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	public void SendToPlayer(MsgDef.Command cmd, ByteString pBuffer, PlayerIF p) {
		try {
//			Set<Entry<String, Player>> players = cache.entrySet();
//			for (Entry<String, Player> entry : players) {
//				Player player = entry.getValue();
//				if (player != null && player.getUserId().equals(p.getTableUser().getUserId())) {
//					player.SendMsgByOther(cmd, pBuffer);
//				}
//			}
			List<Player> players = cache.values();
			for (Player player : players) {
				if (player != null && player.getUserId().equals(p.getTableUser().getUserId())) {
					player.SendMsgByOther(cmd, pBuffer);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 推送给所有在线玩家 */
	public void sendPlayerAll(Command commandId, ByteString byteString) {
		for (Iterator<Entry<String, Player>> it = PlayerMgr.getInstance().getAllPlayer().entrySet().iterator(); it.hasNext();) {
			Entry<String, Player> entry = it.next();
			Player player = ((Player) entry.getValue());
			if (player.getTableUser() != null) {
				player.SendMsg(commandId, byteString);
			}
		}
	}

	/**
	 * 是否在线
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isOnline(String userId) {
		return UserChannelMgr.get(userId)!=null;
	}
}