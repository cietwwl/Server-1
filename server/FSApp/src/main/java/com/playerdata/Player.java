package com.playerdata;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaBM;
import com.bm.player.Observer;
import com.bm.player.ObserverFactory;
import com.bm.player.ObserverFactory.ObserverType;
import com.common.Action;
import com.common.TimeAction;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.assistant.AssistantMgr;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.dataSyn.DataSynVersionHolder;
import com.playerdata.dataSyn.SynDataInReqMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.readonly.EquipMgrIF;
import com.playerdata.readonly.FresherActivityMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.util.DateUtils;
import com.rw.netty.UserChannelMgr;
import com.rw.service.chat.ChatHandler;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.redpoint.RedPointManager;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.PlayerDataMgr;
import com.rwbase.common.RecordSynchronization;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.playerext.PlayerTempAttribute;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.CfgChangeRoleInfoDAO;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.ChangeRoleInfoCfg;
import com.rwbase.dao.user.pojo.LevelCfg;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.CommonMsgProtos.CommonMsgResponse;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.rwproto.ReConnectionProtos.SyncVersion;

/**
 * 玩家类
 *
 * 
 * @inchage Caro
 * @version
 * 
 */

public class Player implements PlayerIF {

	private PlayerMsgMgr playerMsgMgr;
	private UserDataMgr userDataMgr;
	private UserGameDataMgr userGameDataMgr;

	private ItemBagMgr itemBagMgr = new ItemBagMgr();
	private HeroMgr m_HeroMgr = new HeroMgr();
	private CopyRecordMgr m_CopyRecordMgr = new CopyRecordMgr();
	private MagicMgr magicMgr = new MagicMgr();

	private FriendMgr m_friendMgr = new FriendMgr();

	// 延迟初始化的Mgr
	private SignMgr m_SignMgr = new SignMgr();
	private VipMgr m_VipMgr = new VipMgr();
	private SettingMgr m_SettingMgr = new SettingMgr();
	private TowerMgr m_TowerMgr = new TowerMgr();
	// private SecretAreaMgr m_SecretMgr = new SecretAreaMgr();
	private EmailMgr m_emailMgr = new EmailMgr();
	private BattleTowerMgr m_battleTowerMgr = new BattleTowerMgr();
	// private GuildUserMgr m_GuildUserMgr = new GuildUserMgr();
	private GambleMgr m_gambleMgr = new GambleMgr();
	private CopyDataMgr m_CopyDataMgr = new CopyDataMgr();
	private TaskItemMgr m_TaskMgr = new TaskItemMgr();
	private StoreMgr m_StoreMgr = new StoreMgr();
	private DailyActivityMgr m_DailyActivityMgr = new DailyActivityMgr();
	private DailyGifMgr dailyGifMgr = new DailyGifMgr();// 七日礼包

	// 个人帮派数据的Mgr
	private UserGroupAttributeDataMgr userGroupAttributeDataMgr;

	public UnendingWarMgr unendingWarMgr = new UnendingWarMgr();// 无尽战火
	private FashionMgr m_FashionMgr = new FashionMgr();
	private FresherActivityMgr m_FresherActivityMgr = new FresherActivityMgr();// 开服活动

	private AssistantMgr m_AssistantMgr = new AssistantMgr();

	private RedPointMgr redPointMgr = new RedPointMgr();

	private PlayerSaveHelper saveHelper = new PlayerSaveHelper(this);

	private ZoneLoginInfo zoneLoginInfo;

	private volatile long lastWorldChatCacheTime;// 上次世界聊天发送时间
	private volatile long groupRankRecommentCacheTime;// 帮派排行榜推荐的时间
	private volatile long groupRandomRecommentCacheTime;// 帮派排行榜随机推荐的时间
	private volatile int lastWorldChatId;// 聊天上次的版本号
	private volatile long lastGroupChatCacheTime;// 上次帮派聊天发送时间

	private final PlayerTempAttribute tempAttribute;

	class PlayerSaveHelper {

		private Player player;

		private boolean saving = false;

		private AtomicInteger savedCount = new AtomicInteger(0);

		public PlayerSaveHelper(Player playerP) {
			this.player = playerP;
		}

		final int totalToSave = 22;

		public int getProgress() {
			return savedCount.get() * 100 / totalToSave;
		}

		public synchronized int save(boolean immediately) {
			// int progress = 0;
			// if (saving) {
			// progress = getProgress();
			// } else {
			// saving = true;
			// savedCount.set(0);
			// try {
			// // GameLog.error(LogModule.COMMON.getName(),
			// // player.getUserId(), "保存数据。。。。。",null);
			// doSave(immediately);
			// } catch (Exception e) {
			// GameLog.error(LogModule.COMMON.getName(), player.getUserId(),
			// "PlayerSaveHelper[save]用户数据保存错误", e);
			// } finally {
			// saving = false;
			// }
			// }
			// return progress;
			return 0;
		}

		private void doSave(boolean immediately) {
			// TODO 这里应该在内部做判断而不是在外面判null，容易漏掉和不好维护
			// player.getUserGameDataMgr().flush();
			// savedCount.incrementAndGet();
			//
			// player.getUserDataMgr().flush();
			// savedCount.incrementAndGet();
			//
			// player.getItemBagMgr().save();
			// savedCount.incrementAndGet();

			player.getHeroMgr().save(immediately);
			savedCount.incrementAndGet();

			player.getFashionMgr().save();
			savedCount.incrementAndGet();

			player.getMagicMgr().save();
			savedCount.incrementAndGet();

			// player.getFresherActivityMgr().save();
			// savedCount.incrementAndGet();

			if (m_CopyRecordMgr != null) {
				player.getCopyRecordMgr().flush();
				savedCount.incrementAndGet();
			}
			if (m_SettingMgr != null) {
				player.getSettingMgr().flush();
				savedCount.incrementAndGet();
			}

			if (m_CopyDataMgr != null) {
				player.getCopyDataMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_friendMgr != null) {
				player.getFriendMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_VipMgr != null) {
				player.getVipMgr().flush();
				savedCount.incrementAndGet();
			}
			if (m_emailMgr != null) {
				player.getEmailMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_gambleMgr != null) {
				player.getGambleMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_TaskMgr != null) {
				player.getTaskMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_StoreMgr != null) {
				player.getStoreMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_SignMgr != null) {
				player.getSignMgr().save();
				savedCount.incrementAndGet();
			}
			if (m_DailyActivityMgr != null) {
				player.getDailyActivityMgr().save();
				savedCount.incrementAndGet();
			}
			// if (m_TowerMgr != null) {
			// player.getTowerMgr().save();
			// savedCount.incrementAndGet();
			// }
		}
	}

	// private int logoutTimer = 0;

	// 同步数据的版本记录
	private DataSynVersionHolder dataSynVersionHolder = new DataSynVersionHolder();
	private SynDataInReqMgr synDataInReqMgr = new SynDataInReqMgr();

	public static Player newOld(String userId) {
		return new Player(userId, true);
	}

	public void synByVersion(List<SyncVersion> versionList) {
		dataSynVersionHolder.synByVersion(this, versionList);
	}

	public static Player newFresh(String userId) {
		Player fresh = new Player(userId, false);
		fresh.initMgr();
		// 不知道为何，奖励这里也依赖到了任务的TaskMgr,只能初始化完之后再初始化奖励物品
		PlayerFreshHelper.initCreateItem(fresh);
		return fresh;
	}

	private void notifyCreated() {
		Field[] fields = Player.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Object fObject;
			try {
				fObject = f.get(this);
				if (fObject instanceof PlayerEventListener) {
					PlayerEventListener listener = (PlayerEventListener) fObject;
					listener.notifyPlayerCreated(this);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void notifyLogin() {
		Field[] fields = Player.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Object fObject;
			try {
				fObject = f.get(this);
				if (fObject instanceof PlayerEventListener) {
					PlayerEventListener listener = (PlayerEventListener) fObject;
					listener.notifyPlayerLogin(this);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Player(String userId, boolean initMgr, RoleCfg roleCfg) {
		this.tempAttribute = new PlayerTempAttribute();
		playerMsgMgr = new PlayerMsgMgr(userId);
		userDataMgr = new UserDataMgr(this, userId);
		userGameDataMgr = new UserGameDataMgr(this, userId);// 帮派的数据
		userGroupAttributeDataMgr = new UserGroupAttributeDataMgr(getUserId());

		if (!initMgr) {
			MapItemStoreFactory.notifyPlayerCreated(userId);
			this.getHeroMgr().notifyPlayerCreated(this);
			this.getHeroMgr().init(this);
			PlayerFreshHelper.initFreshPlayer(this, roleCfg);
			notifyCreated();
		}

		// 这两个mgr一定要初始化
		m_HeroMgr.init(this);
		itemBagMgr.init(this);
		// 法宝数据
		magicMgr.init(this);
		// 新手礼包，要算英雄个数
		m_FresherActivityMgr.init(this);

		if (initMgr) {
			initMgr();
		}
	}

	public Player(String userId, boolean initMgr) {
		this(userId, initMgr, null);
	}

	public void initMgr() {
		m_CopyRecordMgr.init(this);
		m_CopyDataMgr.init(this);
		m_SignMgr.init(this);
		m_emailMgr.init(this);
		m_DailyActivityMgr.init(this);
		m_friendMgr.init(this);
		m_TowerMgr.init(this);
		// m_SecretMgr.init(this);
		m_StoreMgr.init(this);
		m_SettingMgr.init(this);
		m_VipMgr.init(this);
		unendingWarMgr.init(this);
		dailyGifMgr.init(this);
		m_FashionMgr.init(this);
		m_TaskMgr.init(this);
		m_gambleMgr.init(this);
		// m_GuideMgr.init(this);
		m_AssistantMgr.init(this);
		// m_GuildUserMgr.init(this);
		m_battleTowerMgr.init(this);
		afterMgrInit();

	}

	// 对mgr的初始化有依赖的初始化操作
	private void afterMgrInit() {
		// TODO 这里应该通过接口统一实现
		m_HeroMgr.regAttrChangeCallBack();

		magicMgr.regChangeCallBack(new Action() {

			@Override
			public void doAction() {
				m_HeroMgr.getMainRoleHero().getAttrMgr().reCal();
			}
		});

		m_FashionMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_HeroMgr.getMainRoleHero().getAttrMgr().reCal();
			}
		});
		// initDataVersionControl();
	}

	public void initDataVersionControl() {
		if (!dataSynVersionHolder.isInit()) {
			PlayerDataMgr notInVersionControlP = new PlayerDataMgr(new RecordSynchronization() {

				@Override
				public void synAllData(Player player, int version) {

					onMinutes();
					onNewHour();
					onNewDayZero();
					onNewDay5Clock();

					long now = System.currentTimeMillis();
					userDataMgr.setLastLoginTime(now);
					// 推送帮派的数据
					getUserGroupAttributeDataMgr().synUserGroupData(player);
					// // 推送个人的帮派技能数据
					// getUserGroupAttributeDataMgr().synUserSkillData(player,
					// -1);
					getGambleMgr().syncGamble();
					getSignMgr().onLogin();
					getDailyActivityMgr().onLogin();
					userGameDataMgr.setLastLoginTime(now);
					getFriendMgr().onPlayerChange(player);
					// logoutTimer = 0;
					HotPointMgr.loadPushHotPointState(player);
					WorshipMgr.getInstance().pushByWorshiped(player);
					// TODO HC 聊天信息推送
					ChatHandler.getInstance().sendChatAllMsg(player);
					// 试练塔次数重置
					getBattleTowerMgr().resetBattleTowerResetTimes(now);
				}
			});
			dataSynVersionHolder.init(this, notInVersionControlP);
		}
	}

	public void onLogin() {
		notifyLogin();
		initDataVersionControl();
		onBSStart();// 合并数据同步信息

		try {
			dataSynVersionHolder.synAll(this);
		} finally {
			onBSEnd();
		}

		GroupMemberHelper.onPlayerLogin(this);
		// TODO HC 登录之后检查一下万仙阵的数据
		getTowerMgr().checkAndResetMatchData(this);
		ArenaBM.getInstance().arenaDailyPrize(getUserId(), null);
	}

	public void notifyMainRoleCreation() {
		SendMsg(Command.MSG_DO_MAINROLE_CREATE, null);
	}

	public Hero getMainRoleHero() {
		return m_HeroMgr.getMainRoleHero();
	}

	//
	// public int getLogoutTimer() {
	// return logoutTimer;
	// }
	//
	// public void setLogoutTimer(int logoutTimer) {
	// this.logoutTimer = logoutTimer;
	// }

	// public void Logout() {
	// save();
	// GameLog.debug("Player Logout ...");
	// }

	public void KickOffWithCoolTime(String reason, boolean blnNeedCoolTime) {
		if (blnNeedCoolTime) {
			userDataMgr.setKickOffCoolTime();
		}
		// 修改gm踢人立刻移除在线状态
		KickOffImmediately(reason);
		BILogMgr.getInstance().logZoneLogout(this);

	}

	public void block(String reason, long blockCoolTime) {
		userDataMgr.block(reason, blockCoolTime);
		KickOff(reason);
	}

	public void chatBan(String reason, long blockCoolTime) {
		userDataMgr.chatBan(reason, blockCoolTime);
	}

	public void KickOff(String reason) {
		// 先保存再踢
		save();

		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError(reason);
		SendMsg(Command.MSG_PLAYER_OFF_LINE, loginResponse.build().toByteString());
		// UserChannelMgr.disConnect(getUserId());
	}

	public void KickOffImmediately(String reason) {
		save();

		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError(reason);
		SendMsg(Command.MSG_PLAYER_OFF_LINE, loginResponse.build().toByteString());
		UserChannelMgr.kickoffDisconnect(getUserId());
	}

	private TimeAction onMinutesTimeAction;

	/** 每分钟执行 */
	public synchronized void onMinutes() {

		if (onMinutesTimeAction == null) {
			onMinutesTimeAction = PlayerTimeActionHelper.onMinutes(this);
		}

		onMinutesTimeAction.doAction();
	}

	private TimeAction onNewDayZeroTimeAction;

	/** 0点刷新 */
	public synchronized void onNewDayZero() {
		if (isRobot()) {
			GameLog.info("Player", "#onNewDayZero()", "机器人不进行重置", null);
			return;
		}
		if (onNewDayZeroTimeAction == null) {
			onNewDayZeroTimeAction = PlayerTimeActionHelper.onNewDayZero(this);
		}

		if (isNewDayHour(0, userGameDataMgr.getLastResetTime())) {
			long now = System.currentTimeMillis();
			getUserGameDataMgr().setLastResetTime(now);
			onNewDayZeroTimeAction.doAction();
		}
	}

	private boolean isNewDayHour(int hour, long lastResetTime) {
		return DateUtils.getCurrentHour() >= hour && DateUtils.dayChanged(lastResetTime);
	}

	private TimeAction onNewDay5ClockTimeAction;

	/** 早点５点刷新 */
	public synchronized void onNewDay5Clock() {
		if (isRobot()) {
			GameLog.info("Player", "#onNewDay5Clock()", "机器人不进行重置", null);
			return;
		}

		if (onNewDay5ClockTimeAction == null) {
			onNewDay5ClockTimeAction = PlayerTimeActionHelper.onNewDay5ClockTimeAction(this);
		}

		if (isNewDayHour(5, userGameDataMgr.getLastResetTime5Clock())) {
			long now = System.currentTimeMillis();
			getUserGameDataMgr().setLastResetTime5Clock(now);
			onNewDay5ClockTimeAction.doAction();
		}
	}

	private TimeAction onNewHourTimeAction;

	public synchronized void onNewHour() {
		if (isRobot()) {
			GameLog.info("Player", "#onNewHour()", "机器人不进行重置", null);
			return;
		}

		if (onNewHourTimeAction == null) {
			onNewHourTimeAction = PlayerTimeActionHelper.onNewHour(this);
		}
		onNewHourTimeAction.doAction();

	}

	/**
	 * heartBeat 的时候检查用户需要按时更新的功能
	 */
	public void heartBeatCheck() {
		// getSecretMgr().updateKeyNumByTime();
		// getSecretMgr().updateSecretByTime();
		getAssistantMgr().doCheck();
		if (this.tempAttribute.checkAndResetRedPoint()) {
			RedPointManager.getRedPointManager().checkRedPointVersion(this, this.redPointMgr.getVersion());
		}
	}

	public int save(boolean immediately) {

		return saveHelper.save(immediately);
	}

	public int save() {
		return this.save(false);
	}

	// BusinessService start
	public void onBSStart() {
		synDataInReqMgr.setInReq(true);
	}

	// BusinessService end
	public void onBSEnd() {
		synDataInReqMgr.doSyn(this);
	}

	public void NotifyCommonMsg(ECommonMsgTypeDef type, String message) {
		if (message == null || message.equals("")) {
			return;
		}
		CommonMsgResponse.Builder response = CommonMsgResponse.newBuilder();
		response.setType(type.getValue());
		response.setMessage(message);
		response.setError(ErrorType.SUCCESS);
		GameLog.debug(message);
		// DevelopLogger.info("common message", "player", "", message, "");
		SendMsg(Command.MSG_COMMON_MESSAGE, response.build().toByteString());
	}

	public void NotifyCommonMsg(String message) {
		NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, message);
	}

	public void NotifyCommonMsg(ErrorType type) {
		CommonMsgResponse.Builder response = CommonMsgResponse.newBuilder();
		response.setType(ECommonMsgTypeDef.MsgTips.getValue());
		response.setError(type);
		SendMsg(Command.MSG_COMMON_MESSAGE, response.build().toByteString());
	}

	/**
	 * 非当前玩家发送消息给该玩家，但也有可能是自己发送消息给自己 added by kevin
	 * 
	 * @param Cmd
	 * @param pBuffer
	 */
	public void SendMsgByOther(MsgDef.Command Cmd, ByteString pBuffer) {
		if (StringUtils.isNotBlank(getUserId())) {
			SendMsg(Cmd, pBuffer);
		} else {
			System.out.println("获取玩家userid出现异常！");
		}

	}

	public void SendMsg(MsgDef.Command Cmd, ByteString pBuffer) {
		playerMsgMgr.sendMsg(Cmd, pBuffer, true);
	}

	public int addUserExp(long addExp) {
		int currentLevel = getLevel();// 当前等级
		long currentExp = getExp();// 当前经验

		final int oldLevel = currentLevel;
		final long oldExp = currentExp;
		final long oldAddExp = addExp;

		LevelCfgDAO levelCfgDAO = LevelCfgDAO.getInstance();
		LevelCfg levelCfg = levelCfgDAO.getByLevel(currentLevel);
		if (levelCfg == null) {
			GameLog.error("Player", "addExp", "获取等级配置失败：" + currentLevel + ",应该增加的经验值是：" + addExp, null);
			return -1;
		}

		boolean isFull = false;// 是否等级满了，或者是中间某个等级的数据有问题
		long needExp = levelCfg.getPlayerUpgradeExp();
		while ((currentExp + addExp) >= needExp) {// 当前经验加上增加的经验是否超过了等级需要的经验
			addExp -= (needExp - currentExp);// 超过了之后，剩余的增加经验是要减去升级到等级满经验的差值

			levelCfg = levelCfgDAO.getByLevel(currentLevel + 1);// 加一级，获取不到配置
			if (levelCfg == null) {
				GameLog.error("Player", "addExp", "获取等级配置失败：" + (currentLevel + 1) + ",浪费掉的经验值是：" + addExp + ",应增加经验是：" + oldAddExp, null);
				isFull = true;
				// 不升级，并且把当前经验设置成等级最大经验
				currentExp = needExp;
				break;
			}

			needExp = levelCfg.getPlayerUpgradeExp();// 角色需要的下一级经验

			currentLevel++;// 等级增加
			currentExp = 0;// 当前等级的经验设置为0
		}

		if (!isFull) {
			currentExp += addExp;// 当前经验等于剩余经验
		}

		// 设置当前的经验和等级
		SetLevel(currentLevel);
		SetExp(currentExp);
		if (oldLevel != currentLevel) {
			this.tempAttribute.setLevelChanged();
		}
		if (oldExp != currentExp) {
			this.tempAttribute.setExpChanged();
		}
		GameLog.info("增加主角经验", getUserId(), String.format("之前的等级[%s],经验[%s],增加的经验值是[%s]。新的等级[%s],经验[%s]", oldLevel, oldExp, oldAddExp, currentLevel, currentExp), null);
		// SetCommonAttr(eAttrIdDef.PLAYER_LEVEL, getLevel());
		return 1;
	}

	public void SetExp(long exp) {
		if (exp < 0) {
			exp = 0;
		}
		getMainRoleHero().getRoleBaseInfoMgr().setExp(exp);
	}

	public void SetLevel(int newLevel) {
		// 最高等级
		if (newLevel > PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.PLAYER_MAX_LEVEL)) {
			return;
		}
		int currentLevel = getLevel();
		onLevelChange(currentLevel, newLevel);
		// RankingMgr.getInstance().onExpChange(this);
		getFriendMgr().onPlayerChange(this);

		// 通知一下监听的人，修改对应数据
		Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
		if (observer != null) {
			observer.playerChangeLevel(this);
		}
	}

	// 升级之后业务逻辑
	private void onLevelChange(int currentLevel, int newLevel) {
		// 有升级
		if (currentLevel < newLevel) {
			Hero mainRoleHero = getMainRoleHero();
			// 要先添加体力。再升级
			int addpower = 0;
			for (int i = currentLevel + 1; i <= newLevel; i++) {
				RoleUpgradeCfg upCfg = RoleUpgradeCfgDAO.getInstance().getCfg(i);
				if (upCfg != null) {
					addpower += upCfg.getRecoverPower();
				}
			}
			addPower(addpower);
			mainRoleHero.SetHeroLevel(newLevel);
			userDataMgr.setLevel(newLevel);
			getTaskMgr().initTask();
			getTaskMgr().AddTaskTimes(eTaskFinishDef.Player_Level);
			int quality = RoleQualityCfgDAO.getInstance().getQuality(getMainRoleHero().getQualityId());
			getMainRoleHero().getSkillMgr().activeSkill(newLevel, quality);
			if (mainRoleHero.getTemplateId() != null && currentLevel > 0) {
				// 职业进阶
				// RoleHandler.getInstance().careerAdvance(this, preLevel);
				// 开启转职倒计时
				if (currentLevel < 10 && newLevel >= 10) {
					ChangeRoleInfoCfg changeCfg = CfgChangeRoleInfoDAO.getInstance().getCfg();
					long changeTime = new Date().getTime() + changeCfg.getTime() * 60 * 1000;
					userGameDataMgr.setLastChangeInfoTime(changeTime);
				}
				// this.m_EquipMgr.RefreshEquipEnhance(this, preLevel);
				// 任务
				// m_TaskMgr.addTask();
				m_FresherActivityMgr.doCheck(eActivityType.A_PlayerLv);
			}

			mainRoleHero.save();
			// this.m_EquipMgr.CheckHot();
			getStoreMgr().AddStore();

			// TODO 暂时先通知
			ArenaBM.getInstance().notifyPlayerLevelUp(getUserId(), getCareer(), newLevel);
			BILogMgr.getInstance().logRoleUpgrade(this,currentLevel);
		}
	}

	public void onCareerChange(int career, int sex) {
		try {
			RoleCfg cfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, career, getStarLevel());
			if (cfg == null) {
				NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "配置错误！");
				return;
			}
			getSettingMgr().setCareerHeadImage();
			setTemplateId(cfg.getRoleId());
			SetModelId(cfg.getModelId());
			// 改技能Id
			getMainRoleHero().getSkillMgr().changeSkill(cfg);
			// 新品质 + 可能开放新技能，所以技能ID需要先改变
			String newQuality = cfg.getQualityId().split("_")[0] + "_" + getMainRoleHero().getQualityId().split("_")[1];
			getMainRoleHero().getEquipMgr().EquipAdvance(newQuality, false);
			setStarLevel(cfg.getStarLevel());

			// 任务
			if (cfg.getStarLevel() > getStarLevel()) {
				getTaskMgr().AddTaskTimes(eTaskFinishDef.Player_Quality);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getReward(eSpecialItemId id) {
		long reslut = 0;
		switch (id) {
		case Coin:
			reslut = userGameDataMgr.getCoin();
			break;
		case Gold:
			reslut = userGameDataMgr.getGold();
			break;
		case Power:
			reslut = userGameDataMgr.getPower();
			break;
		case PlayerExp:
			reslut = getExp();
			break;
		case ArenaCoin:
			reslut = userGameDataMgr.getArenaCoin();
			break;
		case BraveCoin:
			reslut = userGameDataMgr.getTowerCoin();
			break;
		case GuildCoin:
			reslut = userGroupAttributeDataMgr.getUserGroupContribution();
			break;
		case PeakArenaCoin:
			reslut = userGameDataMgr.getPeakArenaCoin();
			break;
		case UnendingWarCoin:
			reslut = userGameDataMgr.getUnendingWarCoin();
			break;
		default:
			break;
		}
		return reslut;
	}

	public int getVip() {
		return userDataMgr.getVip();
	}

	public void SetHeadId(String headImage) {
		if (StringUtils.isNotBlank(headImage)) {
			userDataMgr.setHeadId(headImage);
			RankingMgr.getInstance().onPlayerChange(this);
			getFriendMgr().onPlayerChange(this);

			// 通知一下监听的人，修改对应数据
			Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
			if (observer != null) {
				observer.playerChangeHeadIcon(this);
			}
		}
	}

	public void SetUserName(String nick) {
		if (StringUtils.isNotBlank(nick)) {
			userDataMgr.setUserName(nick);
			RankingMgr.getInstance().onPlayerChange(this);
			getFriendMgr().onPlayerChange(this);

			// 通知一下监听的人，修改对应数据
			Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
			if (observer != null) {
				observer.playerChangeName(this);
			}
		}
	}

	public int setVip(int vip) {
		if (vip >= PrivilegeCfgDAO.getInstance().getMinVip() && vip <= PrivilegeCfgDAO.getInstance().getMaxVip()) {
			int oldVip = getVip();
			userDataMgr.setVip(vip);
			getVipMgr().upgradeVipRefreshPrivilege(oldVip);
			if (oldVip < vip) {
				getStoreMgr().AddStore();
			}

			// 通知一下监听的人，修改对应数据
			Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
			if (observer != null) {
				observer.playerChangeVipLevel(this);
			}
			return 0;
		}
		return -1;
	}

	public int getSex() {
		return userDataMgr.getSex();
	}

	public TableUserIF getTableUser() {
		return userDataMgr.getReadOnly();
	}

	public int AddVip(int nValue) {
		int addValue = getVip() + nValue;
		return setVip(addValue);
	}

	/**
	 * 升星
	 */
	public void setStarLevel(int starLevel) {
		getMainRoleHero().getRoleBaseInfoMgr().setStarLevel(starLevel);
	}

	public int getStarLevel() {
		// return 0;
		return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getStarLevel();
	}

	public int getLevel() {
		return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getLevel();
	}

	public long getExp() {
		return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getExp();
	}

	public String getUserName() {
		return userDataMgr.getUserName();
	}

	public String getHeadImage() {
		return userDataMgr.getHeadImage();
	}

	public int getCareer() {
		return getMainRoleHero().getCareer();
	}

	public String getTemplateId() {
		return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getTemplateId();

	}

	public void SetCareer(int career) {
		getMainRoleHero().getRoleBaseInfoMgr().setCareerType(career);
		RankingMgr.getInstance().onPlayerChange(this);
		getFriendMgr().onPlayerChange(this);
	}

	public int AddRecharge(int nValue) {

		int totalValue = userGameDataMgr.getRecharge() + nValue;
		if (totalValue >= 0) {
			int value = totalValue;
			PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(getVip() + 1);
			while (cfg.getRechargeCount() <= value) {
				AddVip(1);
				value -= cfg.getRechargeCount();
				cfg = PrivilegeCfgDAO.getInstance().getCfg(getVip() + 1);
			}
			// 设置界面更新vip
			getSettingMgr().checkOpen();
			if (totalValue > userGameDataMgr.getRecharge()) {
				getTaskMgr().AddTaskTimes(eTaskFinishDef.Recharge);
			}
			userGameDataMgr.setRecharge(nValue);
			return 0;
		}
		return -1;
	}

	public boolean addPower(int value) {
		return userGameDataMgr.addPower(value, getLevel());
	}

	public void AddBuyCoinTimes(int nValue) {
		// 记录购买点金手的次数
		getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Gold_Point, nValue);
		userGameDataMgr.addBuyCoinTimes(nValue);
	}

	public String getUserId() {
		return userGameDataMgr.getUserId();
	}

	public TableUserOtherIF getTableUserOther() {
		return userGameDataMgr.getReadOnly();
	}

	public void setTemplateId(String templateId) {
		if (templateId != null) {
			getMainRoleHero().getRoleBaseInfoMgr().setTemplateId(templateId);

			// 通知一下监听的人，修改对应数据
			Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
			if (observer != null) {
				observer.playerChangeTemplateId(this);
			}
		}
	}

	public void SetModelId(int modelId) {
		if (modelId > 0) {
			getMainRoleHero().getRoleBaseInfoMgr().setModelId(modelId);
			RankingMgr.getInstance().onPlayerChange(this);
		}
	}

	public int getModelId() {
		return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getModeId();
	}

	public DailyActivityMgr getDailyActivityMgr() {
		return m_DailyActivityMgr;
	}

	public FriendMgr getFriendMgr() {
		return this.m_friendMgr;
	}

	public StoreMgr getStoreMgr() {
		return m_StoreMgr;
	}

	public TaskItemMgr getTaskMgr() {
		return m_TaskMgr;
	}

	public GambleMgr getGambleMgr() {
		return m_gambleMgr;
	}

	public VipMgr getVipMgr() {
		return m_VipMgr;
	}

	public DataSynVersionHolder getDataSynVersionHolder() {
		return dataSynVersionHolder;
	}

	public UserGameDataMgr getUserGameDataMgr() {
		return userGameDataMgr;
	}

	public FashionMgr getFashionMgr() {
		return m_FashionMgr;
	}

	public ItemBagMgr getItemBagMgr() {
		return itemBagMgr;
	}

	public HeroMgr getHeroMgr() {
		return m_HeroMgr;
	}

	public BattleTowerMgr getBattleTowerMgr() {
		return this.m_battleTowerMgr;
	}

	public CopyRecordMgr getCopyRecordMgr() {
		return m_CopyRecordMgr;
	}

	public CopyDataMgr getCopyDataMgr() {
		return m_CopyDataMgr;
	}

	public SignMgr getSignMgr() {
		return m_SignMgr;
	}

	public SettingMgr getSettingMgr() {
		return m_SettingMgr;
	}

	public AttrMgr getAttrMgr() {
		return getMainRoleHero().getAttrMgr();
	}

	public SkillMgr getSkillMgr() {
		return getMainRoleHero().getSkillMgr();
	}

	public EmailMgr getEmailMgr() {
		return m_emailMgr;
	}

	public TowerMgr getTowerMgr() {
		return m_TowerMgr;
		// return null;
	}

	public SecretAreaMgr getSecretMgr() {
		// if (m_SecretMgr == null) {
		// m_SecretMgr = new SecretAreaMgr();
		// m_SecretMgr.init(this);
		// }
		// return m_SecretMgr;
		return null;
	}

	public MagicMgr getMagicMgr() {
		return this.magicMgr;
	}

	@Override
	public EquipMgrIF getEquipMgr() {
		return getMainRoleHero().getEquipMgr();
	}

	@Override
	public ItemData getMagic() {
		return magicMgr.getMagic();
	}

	public UserDataMgr getUserDataMgr() {
		return userDataMgr;
	}

	public SynDataInReqMgr getSynDataInReqMgr() {
		return synDataInReqMgr;
	}

	public void setSynDataInReqMgr(SynDataInReqMgr synDataInReqMgr) {
		this.synDataInReqMgr = synDataInReqMgr;
	}

	public PlayerMsgMgr getPlayerMsgMgr() {
		return playerMsgMgr;
	}

	public RedPointMgr getRedPointMgr() {
		return redPointMgr;
	}

	public GuildUserMgr getGuildUserMgr() {
		return null;
	}

	public FresherActivityMgr getFresherActivityMgr() {
		return m_FresherActivityMgr;
		// return null;
	}

	public FresherActivityMgrIF getFresherActivityMgrIF() {
		return m_FresherActivityMgr;
	}

	public AssistantMgr getAssistantMgr() {
		return m_AssistantMgr;
	}

	public DailyGifMgr getDailyGifMgr() {
		return dailyGifMgr;
	}

	public UnendingWarMgr getUnendingWarMgr() {
		return unendingWarMgr;
	}

	public ZoneLoginInfo getZoneLoginInfo() {
		return zoneLoginInfo;
	}

	public void setZoneLoginInfo(ZoneLoginInfo zoneLoginInfo) {
		this.zoneLoginInfo = zoneLoginInfo;
	}

	/**
	 * 获取个人的帮派数据
	 * 
	 * @return
	 */
	public UserGroupAttributeDataMgr getUserGroupAttributeDataMgr() {
		return userGroupAttributeDataMgr;
	}

	/**
	 * 获取上次世界聊天发言的时间
	 * 
	 * @return
	 */
	public long getLastWorldChatCacheTime() {
		return lastWorldChatCacheTime;
	}

	/**
	 * 设置上次世界聊天发言的时间
	 * 
	 * @param lastWorldChatCacheTime
	 */
	public void setLastWorldChatCacheTime(long lastWorldChatCacheTime) {
		this.lastWorldChatCacheTime = lastWorldChatCacheTime;
	}

	/**
	 * 获取上次帮派聊天的时间
	 * 
	 * @return
	 */
	public long getLastGroupChatCacheTime() {
		return lastGroupChatCacheTime;
	}

	/**
	 * 设置上次帮派聊天的时间
	 * 
	 * @param lastGroupChatCacheTime
	 */
	public void setLastGroupChatCacheTime(long lastGroupChatCacheTime) {
		this.lastGroupChatCacheTime = lastGroupChatCacheTime;
	}

	public int getLastWorldChatId() {
		return lastWorldChatId;
	}

	public void setLastWorldChatId(int lastWorldChatId) {
		this.lastWorldChatId = lastWorldChatId;
	}

	/**
	 * 上次帮派排行榜推荐刷新的时间
	 * 
	 * @return
	 */
	public long getGroupRankRecommentCacheTime() {
		return groupRankRecommentCacheTime;
	}

	/**
	 * 获取帮派排行榜推荐刷新的时间
	 * 
	 * @param groupRankRecommentCacheTime
	 */
	public void setGroupRankRecommentCacheTime(long groupRankRecommentCacheTime) {
		this.groupRankRecommentCacheTime = groupRankRecommentCacheTime;
	}

	/**
	 * 上次帮派推荐随机刷新的时间
	 * 
	 * @return
	 */
	public long getGroupRandomRecommentCacheTime() {
		return groupRandomRecommentCacheTime;
	}

	/**
	 * 获取帮派排行榜推荐刷新的时间
	 * 
	 * @param groupRandomRecommentCacheTime
	 */
	public void setGroupRandomRecommentCacheTime(long groupRandomRecommentCacheTime) {
		this.groupRandomRecommentCacheTime = groupRandomRecommentCacheTime;
	}

	/**
	 * 获取玩家的临时属性
	 * 
	 * @return
	 */
	public PlayerTempAttribute getTempAttribute() {
		return tempAttribute;
	}

	@Override
	public String toString() {
		return "[" + getUserName() + "][" + getUserId() + "]";
	}

	/**
	 * 临时方法
	 * 
	 * @return
	 */
	public boolean isRobot() {
		return getUserId().length() > 20;
	}
}