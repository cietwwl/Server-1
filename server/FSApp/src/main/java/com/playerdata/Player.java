package com.playerdata;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaBM;
import com.bm.player.Observer;
import com.bm.player.ObserverFactory;
import com.bm.player.ObserverFactory.ObserverType;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.worldBoss.WBMgr;
import com.common.Action;
import com.common.TimeAction;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.assistant.AssistantMgr;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.dataSyn.DataSynVersionHolder;
import com.playerdata.dataSyn.UserTmpGameDataFlag;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.group.UserGroupCopyMapRecordMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.playerdata.groupsecret.GroupSecretTeamDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.hero.core.FSHeroBaseInfoMgr;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.hero.core.FSUserHeroGlobalDataMgr;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.readonly.EquipMgrIF;
import com.playerdata.readonly.FresherActivityMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.fsutil.util.DateUtils;
import com.rw.netty.UserChannelMgr;
import com.rw.routerServer.giftManger.RouterGiftMgr;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.Privilege.IPrivilegeManager;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rw.service.Privilege.PrivilegeManager;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.chat.ChatHandler;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rw.service.guide.NewGuideStateChecker;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.magicEquipFetter.MagicEquipFetterMgr;
import com.rw.service.redpoint.RedPointManager;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.PlayerDataMgr;
import com.rwbase.common.RecordSynchronization;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.playerext.PlayerTempAttribute;
import com.rwbase.dao.fetters.HeroFettersData;
import com.rwbase.dao.fetters.HeroFettersDataHolder;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.groupsecret.pojo.GroupSecretBaseInfoSynDataHolder;
import com.rwbase.dao.groupsecret.pojo.GroupSecretTeamInfoSynDataHolder;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.openLevelTiggerService.OpenLevelTiggerServiceRegeditInfo;
import com.rwbase.dao.power.PowerInfoDataHolder;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.PowerInfo;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.praise.PraiseMgr;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.CfgChangeRoleInfoDAO;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.pojo.ChangeRoleInfoCfg;
import com.rwbase.dao.user.pojo.LevelCfg;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
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

	private final String userId; // 角色id
	private int level;
	private UserDataMgr userDataMgr;
	private UserGameDataMgr userGameDataMgr;

	private HeroMgr m_HeroMgr = FSHeroMgr.getInstance();
	private CopyRecordMgr m_CopyRecordMgr = new CopyRecordMgr();
	private MagicMgr magicMgr = new MagicMgr();
	private FriendMgr m_friendMgr = new FriendMgr();

	// 延迟初始化的Mgr
	private SignMgr m_SignMgr = new SignMgr();
	private VipMgr m_VipMgr = new VipMgr();
	private SettingMgr m_SettingMgr = new SettingMgr();
	private TowerMgr m_TowerMgr = new TowerMgr();
	private EmailMgr m_emailMgr = new EmailMgr();
	private BattleTowerMgr m_battleTowerMgr = new BattleTowerMgr();
	private GambleMgr m_gambleMgr = new GambleMgr();
	private CopyDataMgr m_CopyDataMgr = new CopyDataMgr();
	private TaskItemMgr m_TaskMgr = new TaskItemMgr();
	private StoreMgr m_StoreMgr = new StoreMgr();
	private DailyActivityMgr m_DailyActivityMgr = new DailyActivityMgr();
	private DailyGifMgr dailyGifMgr = new DailyGifMgr();// 七日礼包
	private MagicEquipFetterMgr me_FetterMgr = new MagicEquipFetterMgr();
	private HeroFettersMgr heroFettersMgr = new HeroFettersMgr();// 英雄羁绊的Mgr

	// 特权管理器
	private PrivilegeManager privilegeMgr = new PrivilegeManager();
	private GuidanceMgr guideMgr = new GuidanceMgr();

	// 个人帮派数据的Mgr
	private UserGroupAttributeDataMgr userGroupAttributeDataMgr;
	private UserGroupCopyMapRecordMgr userGroupCopyRecordMgr;

	public UnendingWarMgr unendingWarMgr = new UnendingWarMgr();// 无尽战火
	private FashionMgr m_FashionMgr = new FashionMgr();
	private FresherActivityMgr m_FresherActivityMgr = new FresherActivityMgr();// 开服活动

	private AssistantMgr m_AssistantMgr = new AssistantMgr();

	private RedPointMgr redPointMgr = new RedPointMgr();

	private TaoistMgr taoistMgr = new TaoistMgr();

	private UpgradeMgr upgradeMgr = new UpgradeMgr();

	// 客户端管理工具
	private PlayerQuestionMgr playerQuestionMgr = new PlayerQuestionMgr();

	private OpenLevelTiggerServiceRegeditInfo openLevelTiggerServiceRegeditInfo;

	private volatile long lastWorldChatCacheTime;// 上次世界聊天发送时间
	private volatile long groupRankRecommentCacheTime;// 帮派排行榜推荐的时间
	private volatile long groupRandomRecommentCacheTime;// 帮派排行榜随机推荐的时间
	private volatile int lastWorldChatId;// 聊天上次的版本号
	private volatile long lastGroupChatCacheTime;// 上次帮派聊天发送时间
	private volatile long lastTeamChatCahceTime;// 上次发送组队聊天时间

	private TimeAction oneSecondTimeAction;// 秒时效

	private final PlayerTempAttribute tempAttribute;

	private PowerInfo powerInfo;// 体力信息，仅仅用于同步到前台数据

	private UserTmpGameDataFlag userTmpGameDataFlag = new UserTmpGameDataFlag();// 用户临时数据的同步

	// /** 羁绊的缓存数据<英雄的ModelId,List<羁绊的推送数据>> */
	// private ConcurrentHashMap<Integer, SynFettersData> fettersMap = new ConcurrentHashMap<Integer, SynFettersData>(); private int logoutTimer = 0;

	// 同步数据的版本记录
	private DataSynVersionHolder dataSynVersionHolder = new DataSynVersionHolder();

	// 个人帮派秘境的Holder
	private GroupSecretBaseInfoSynDataHolder baseHolder = new GroupSecretBaseInfoSynDataHolder();
	private GroupSecretTeamInfoSynDataHolder teamHolder = new GroupSecretTeamInfoSynDataHolder();

	private AtomicInteger itemGenerateId;

	public void synByVersion(List<SyncVersion> versionList) {
		dataSynVersionHolder.synByVersion(this, versionList);
	}

	public static Player newFresh(String userId, ZoneLoginInfo zoneLoginInfo2) {

		Player fresh = new Player(userId, false);
		// 楼下的好巧啊.初始化的任务会触发taskbegin，但日志所需信息需要player来set，这里粗暴点
		User user = fresh.getUserDataMgr().getUser();
		user.setZoneLoginInfo(zoneLoginInfo2);

		fresh.initMgr();
		return fresh;
	}

	//
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
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public Player(final String userId, boolean loadFromDB, RoleCfg roleCfg, PlayerCreateParam createParam) {
		// long start = System.currentTimeMillis();
		this.userId = userId;
		this.userDataMgr = new UserDataMgr(this, userId);
		if (!loadFromDB) {
			MapItemStoreFactory.notifyPlayerCreated(userId);
		}
		if (createParam != null) {
			this.level = createParam.getLevel();
		}
		this.tempAttribute = new PlayerTempAttribute();
		userGameDataMgr = new UserGameDataMgr(this, userId);// 帮派的数据
		userGroupAttributeDataMgr = new UserGroupAttributeDataMgr(getUserId());
		userGroupCopyRecordMgr = new UserGroupCopyMapRecordMgr(getUserId());
		if (!loadFromDB) {
			PlayerFreshHelper.initFreshPlayer(this, roleCfg);
			RoleExtPropertyFactory.firstCreatePlayerExtProperty(userId, userDataMgr.getCreateTime(), getLevel());
			notifyCreated();
		}

		// 法宝数据
		magicMgr.init(this);
		// 新手礼包，要算英雄个数
		m_FresherActivityMgr.init(this);

		playerQuestionMgr.init(this);

		if (loadFromDB) {
			initMgr();
		}

		this.oneSecondTimeAction = PlayerTimeActionHelper.onSecond(this);

		powerInfo = new PowerInfo(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_RECOVER_TIME));

		// 初始化角色最大的道具Id
		itemGenerateId = new AtomicInteger(ItemBagMgr.getInstance().initMaxId(userId));
	}

	public Player(String userId, boolean initMgr) {
		this(userId, initMgr, null, null);
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
		taoistMgr.init(this);
		m_gambleMgr.init(this);
		// m_GuideMgr.init(this);
		m_AssistantMgr.init(this);
		// m_GuildUserMgr.init(this);
		m_battleTowerMgr.init(this);

		guideMgr.init(this);

		afterMgrInit();
		upgradeMgr.init(this);

		privilegeMgr.init(this);
		me_FetterMgr.init(this);// 注意，这个要求加载完法宝及神器数据，因为会在内部对这个模块数据进行检查-----by
								// Alex
		m_TaskMgr.init(this);// 任务要获取其他模块的数据，所以把它放在最后进行初始化 ---by Alex
	}

	// 对mgr的初始化有依赖的初始化操作
	private void afterMgrInit() {
		// TODO 这里应该通过接口统一实现
		m_HeroMgr.regAttrChangeCallBack();

		magicMgr.regChangeCallBack(new Action() {

			@Override
			public void doAction() {
				// m_HeroMgr.getMainRoleHero().getAttrMgr().reCal();
				Enumeration<? extends Hero> heros = m_HeroMgr.getHerosEnumeration(Player.this);
				while (heros.hasMoreElements()) {
					heros.nextElement().getAttrMgr().reCal();
				}
			}
		});

		me_FetterMgr.reChangeCallBack(new Action() {

			@Override
			public void doAction() {

				Enumeration<? extends Hero> heros = m_HeroMgr.getHerosEnumeration(Player.this);
				while (heros.hasMoreElements()) {
					heros.nextElement().getAttrMgr().reCal();
				}
			}
		});

		m_FashionMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				Player.this.getMainRoleHero().getAttrMgr().reCal();
			}
		});

		taoistMgr.getEff().subscribe(new IStreamListner<Map<Integer, AttributeItem>>() {
			@Override
			public void onChange(Map<Integer, AttributeItem> newValue) {
				Enumeration<? extends Hero> heros = m_HeroMgr.getHerosEnumeration(Player.this);
				while (heros.hasMoreElements()) {
					heros.nextElement().getAttrMgr().reCal();
				}
			}

			@Override
			public void onClose(IStream<Map<Integer, AttributeItem>> whichStream) {
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
					getGambleMgr().syncMainCityGambleHotPoint();
					getSignMgr().onLogin();
					getDailyActivityMgr().onLogin();
					getFriendMgr().onPlayerChange(player);
					// logoutTimer = 0;
					WorshipMgr.getInstance().pushByWorshiped(player);
					// TODO HC 聊天信息推送
					ChatHandler.getInstance().sendChatAllMsg(player);
					// 试练塔次数重置
					getBattleTowerMgr().resetBattleTowerResetTimes(now);
					// 登录之后推送体力信息
					PowerInfoDataHolder.synPowerInfo(player);
					// 登录推送所有的羁绊属性
					HeroFettersDataHolder.synAll(player);
					// 登陆检查通用活动的所有相关，包括根据新配置表创建新纪录；用新版本表刷新老纪录；给老纪录未领奖用户补发奖励以及关闭红点
					ActivityCountTypeMgr.getInstance().checkActivity(player);
					// OpenLevelTiggerServiceMgr.getInstance().regeditByLogin(player);//数据不分开处理前，初始化会在好友处处理；策划给出大量相似引导需求后需剥离开后并在此注册

					m_AssistantMgr.synData();

					// 推送帮派秘境基础数据
					UserGroupSecretBaseDataMgr.getMgr().synData(player);
					// 推送帮派秘境的Team信息
					GroupSecretTeamDataMgr.getMgr().synData(player);

					// 为了处理掉线的情况，这里要处理一下帮派争霸的数据
					GCompMatchDataHolder.getHolder().synPlayerMatchData(player);
					// 当登录的时候，处理一下点赞的数据
					PraiseMgr.getMgr().synData(player);
					// 发送角色的全局数据
					FSUserHeroGlobalDataMgr.getInstance().synData(player);
					//发送屏蔽新手引导信息
					NewGuideStateChecker.getInstance().check(player, true);

				}
			});
			dataSynVersionHolder.init(this, notInVersionControlP);
		}
	}

	public ByteString onLogin(Object recordKey) {
		String userId = getUserId();
		ByteString synData = null;
		UserChannelMgr.onBSBegin(userId);
		try {
			notifyLogin();
			initDataVersionControl();
			dataSynVersionHolder.synAll(this);
			// 检查主角羁绊
			this.me_FetterMgr.checkPlayerData(this);
			GroupMemberHelper.onPlayerLogin(this);
			ArenaBM.getInstance().arenaDailyPrize(getUserId(), null);
			// TODO HC 登录之后检查一下万仙阵的数据
			getTowerMgr().checkAndResetMatchData(this);
			// 当角色登录的时候，更新下登录的时间
			AngelArrayTeamInfoHelper.updateRankingEntry(this, AngelArrayTeamInfoCall.loginCall);
			// 角色登录检查秘境数据是否可以重置
			UserGroupSecretBaseDataMgr.getMgr().checkCanReset(this, System.currentTimeMillis());
			// 时效任务的角色登录
			com.rwbase.common.timer.core.FSGameTimerMgr.getInstance().playerLogin(this);
			// 帮派争霸角色登录通知
			GroupCompetitionMgr.getInstance().onPlayerLogin(this);

			WBMgr.getInstance().onPlayerLogin(this);
			RouterGiftMgr.getInstance().takeGift(userId);
		} finally {
			synData = UserChannelMgr.getDataOnBSEnd(userId, recordKey);
		}

		return synData;
	}

	public void notifyMainRoleCreation() {
		SendMsg(Command.MSG_DO_MAINROLE_CREATE, null);
	}

	public Hero getMainRoleHero() {
		return m_HeroMgr.getMainRoleHero(this);
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
		String error = "亲爱的用户，抱歉你已被封号。请联系我们的客服。";
		if (reason != null) {
			error = reason;
		}
		error = "封号原因:" + error;
		String releaseTime;
		if (blockCoolTime > 0) {
			releaseTime = "解封时间:" + DateUtils.getDateTimeFormatString(blockCoolTime, "yyyy-MM-dd HH:mm");
		} else {
			releaseTime = "解封时间:永久封号!";
		}
		error += "\n" + releaseTime;
		KickOff(error);
	}

	public void chatBan(String reason, long blockCoolTime) {
		userDataMgr.chatBan(reason, blockCoolTime);
	}

	public void KickOff(String reason) {
		// 先保存再踢
		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError(reason);
		SendMsg(Command.MSG_PLAYER_OFF_LINE, loginResponse.build().toByteString());
		// UserChannelMgr.disConnect(getUserId());
	}

	public void KickOffImmediately(String reason) {

		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError(reason);
		SendMsg(Command.MSG_PLAYER_OFF_LINE, loginResponse.build().toByteString());
		UserChannelMgr.kickoffDisconnect(getUserId());
	}

	private TimeAction onMinutesTimeAction;

	/** 每分钟执行 */
	public void onMinutes() {

		if (onMinutesTimeAction == null) {
			onMinutesTimeAction = PlayerTimeActionHelper.onMinutes(this);
		}

		onMinutesTimeAction.doAction();
	}

	private TimeAction onNewDayZeroTimeAction;

	/** 0点刷新 */
	public void onNewDayZero() {
		if (isRobot()) {
			GameLog.info("Player", "#onNewDayZero()", "机器人不进行重置", null);
			return;
		}
		if (onNewDayZeroTimeAction == null) {
			onNewDayZeroTimeAction = PlayerTimeActionHelper.onNewDayZero(this);
		}

		if (DateUtils.isNewDayHour(0, userGameDataMgr.getLastResetTime())) {
			long now = System.currentTimeMillis();
			getUserGameDataMgr().setLastResetTime(now);
			onNewDayZeroTimeAction.doAction();
		}
	}

	private TimeAction onNewDay5ClockTimeAction;

	/** 早点５点刷新 */
	public void onNewDay5Clock() {
		if (isRobot()) {
			GameLog.info("Player", "#onNewDay5Clock()", "机器人不进行重置", null);
			return;
		}

		if (onNewDay5ClockTimeAction == null) {
			onNewDay5ClockTimeAction = PlayerTimeActionHelper.onNewDay5ClockTimeAction(this);
		}

		if (DateUtils.isNewDayHour(5, userGameDataMgr.getLastResetTime5Clock())) {
			long now = System.currentTimeMillis();
			getUserGameDataMgr().setLastResetTime5Clock(now);
			onNewDay5ClockTimeAction.doAction();
			UserGroupSecretBaseDataMgr.getMgr().checkCanReset(this, now);
		}
	}

	private TimeAction onNewHourTimeAction;

	public void onNewHour() {
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

		ActivityTimeCountTypeMgr.getInstance().doTimeCount(this, ActivityTimeCountTypeEnum.role_online);
		getAssistantMgr().doCheck();
		if (this.tempAttribute.checkAndResetRedPoint()) {
			RedPointManager.getRedPointManager().checkRedPointVersion(this, this.redPointMgr.getVersion());
		}

		{// 检查巅峰竞技场
			Player player = this;
			PeakArenaBM peakBM = PeakArenaBM.getInstance();
			TablePeakArenaData arenaData = peakBM.getOrAddPeakArenaData(player);
			if (arenaData != null) {
				peakBM.addPeakArenaCoin(player, arenaData, peakBM.getPlace(player), System.currentTimeMillis());
			}
		}
	}

	public void NotifyCommonMsg(ErrorType error, ECommonMsgTypeDef msgShowType, String message) {
		CommonMsgResponse.Builder response = CommonMsgResponse.newBuilder();
		response.setType(msgShowType.getValue());
		response.setError(error);
		response.setMessage(message);
		SendMsg(Command.MSG_COMMON_MESSAGE, response.build().toByteString());
	}

	public void NotifyFunctionNotOpen(String message) {
		NotifyCommonMsg(ErrorType.FUNCTION_NOT_OPEN, ECommonMsgTypeDef.MsgTips, message);
	}

	public void NotifyCommonMsg(ECommonMsgTypeDef type, String message) {
		if (StringUtils.isBlank(message)) {
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

	public void SendMsg(MsgDef.Command Cmd, ByteString pBuffer) {
		SendMsg(Cmd, null, pBuffer);
	}

	public void SendMsg(MsgDef.Command Cmd, Object subCmd, ByteString pBuffer) {
		try {
			String userId = getUserId();
			UserChannelMgr.sendAyncResponse(userId, Cmd, subCmd, pBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		// getMainRoleHero().getRoleBaseInfoMgr().setExp(exp);
		FSHeroBaseInfoMgr.getInstance().setExp(getMainRoleHero(), exp);
	}

	public OpenLevelTiggerServiceRegeditInfo getOpenLevelTiggerServiceRegeditInfo() {
		return openLevelTiggerServiceRegeditInfo;
	}

	public void setOpenLevelTiggerServiceRegeditInfo(OpenLevelTiggerServiceRegeditInfo openLevelTiggerServiceRegeditInfo) {
		this.openLevelTiggerServiceRegeditInfo = openLevelTiggerServiceRegeditInfo;
	}

	// by franky 升级通知，响应时可以通过sample方法获取旧的等级
	private StreamImpl<Integer> levelNotification = new StreamImpl<Integer>();

	public IStream<Integer> getLevelNotification() {
		return levelNotification;
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

		levelNotification.fire(newLevel);
	}

	// 升级之后业务逻辑
	private void onLevelChange(int currentLevel, int newLevel) {
		// 有升级
		if (currentLevel < newLevel) {
			int fightbeforelevelup = getHeroMgr().getFightingTeam(this);
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
			this.level = newLevel;
			// mainRoleHero.SetHeroLevel(newLevel);
			FSHeroBaseInfoMgr.getInstance().setLevel(mainRoleHero, newLevel);
			userDataMgr.setLevel(newLevel);
			MagicChapterInfoHolder.getInstance().synAllData(this);
			getTaskMgr().checkAndAddList();
			getTaskMgr().AddTaskTimes(eTaskFinishDef.Player_Level);

			// 升级添加日常任务通知,刷新一下任务红点----by Alex
			m_DailyActivityMgr.resRed();

			int quality = RoleQualityCfgDAO.getInstance().getQuality(getMainRoleHero().getQualityId());
			getMainRoleHero().getSkillMgr().activeSkill(this, getMainRoleHero().getUUId(), newLevel, quality);
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
			BILogMgr.getInstance().logRoleUpgrade(this, currentLevel, fightbeforelevelup);
		}
	}

	public void setLevelByGM(int newLevel) {
		if (newLevel <= 0) {
			return;
		}
		// 最高等级
		if (newLevel > PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.PLAYER_MAX_LEVEL)) {
			return;
		}
		int currentLevel = getLevel();

		onLevelChangeByGm(currentLevel, newLevel);
		getFriendMgr().onPlayerChange(this);

		// 通知一下监听的人，修改对应数据
		Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
		if (observer != null) {
			observer.playerChangeLevel(this);
		}

		levelNotification.fire(newLevel);

	}

	private void onLevelChangeByGm(int currentLevel, int newLevel) {
		// 有升级
		if (currentLevel < newLevel) {
			onLevelChange(currentLevel, newLevel);
		} else {
			Hero mainRoleHero = getMainRoleHero();
			int fightbeforelevelup = getHeroMgr().getFightingTeam(this);
			// mainRoleHero.SetHeroLevel(newLevel);
			FSHeroBaseInfoMgr.getInstance().setLevel(mainRoleHero, newLevel);
			userDataMgr.setLevel(newLevel);
			level = newLevel;// GM指令之后把缓存的等级修改成新等级
			mainRoleHero.save();
			ArenaBM.getInstance().notifyPlayerLevelUp(getUserId(), getCareer(), newLevel);
			BILogMgr.getInstance().logRoleUpgrade(this, currentLevel, fightbeforelevelup);
		}

	}

	public void onCareerChange(int career, int sex) {
		try {
			// int oldModelId = getModelId();
			RoleCfg cfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, career, getStarLevel());
			if (cfg == null) {
				NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "配置错误！");
				return;
			}
			getSettingMgr().setCareerHeadImage();
			setTemplateId(cfg.getRoleId());
			SetModelId(cfg.getModelId());

			// 改技能Id：需要先改变技能，后续的战斗力计算会获取最新的技能列表，并且会根据角色的templateId去获取普攻技能id
			// 因为上面已经把角色的模板id修改了，而其他的通知有可能会通知重新计算战力，计算战力的时候会获取技能列表
			// 如果这个时候技能列表还是旧的，就会筛选不出普攻，这样计算战力的地方会出现异常，暂时没有筛选普攻的更好方式，所以先保证技能转换放在第一位
			getMainRoleHero().getSkillMgr().changeSkill(this, this.getMainRoleHero().getUUId(), cfg);
			// 附灵通知
			SpriteAttachMgr.getInstance().onCarrerChange(this);
			// 新品质 + 可能开放新技能，所以技能ID需要先改变
			String newQuality = cfg.getQualityId().split("_")[0] + "_" + getMainRoleHero().getQualityId().split("_")[1];
			getMainRoleHero().getEquipMgr().EquipAdvance(this, this.getMainRoleHero().getUUId(), newQuality, false);
			setStarLevel(cfg.getStarLevel());

			getMainRoleHero().getFixNormEquipMgr().onCarrerChange(this);
			getMainRoleHero().getFixExpEquipMgr().onCarrerChange(this);

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
		case MagicSecretCoin:
			reslut = userGameDataMgr.getMagicSecretCoin();
			break;
		case TEAM_BATTLE_GOLD:
			reslut = userGameDataMgr.getTeamBattleCoin();
			break;
		case WAKEN_KEY:
			reslut = userGameDataMgr.getWakenKey();
			break;
		case WAKEN_PIECE:
			reslut = userGameDataMgr.getWakenPiece();
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

	public boolean SetUserName(String nick) {
		if (StringUtils.isNotBlank(nick)) {
			boolean result = userDataMgr.setUserName(nick);
			if (result) {
				RankingMgr.getInstance().onPlayerChange(this);
				getFriendMgr().onPlayerChange(this);

				// 通知一下监听的人，修改对应数据
				Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
				if (observer != null) {
					observer.playerChangeName(this);
				}
			}
			return result;
		}
		return false;
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
			RankingMgr.getInstance().onPlayerChange(this);
			getFriendMgr().onPlayerChange(this);
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
		// getMainRoleHero().getRoleBaseInfoMgr().setStarLevel(starLevel);
		FSHeroBaseInfoMgr.getInstance().setStarLevel(getMainRoleHero(), starLevel);
	}

	public int getStarLevel() {
		// return 0;
		// return
		// getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getStarLevel();
		return getMainRoleHero().getStarLevel();
	}

	public int getLevel() {
		// return
		// getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getLevel();
		if (level == 0) {
			level = getMainRoleHero().getLevel();
		}
		return level;
	}

	public long getExp() {
		// return getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getExp();
		return getMainRoleHero().getExp();
	}

	public String getUserName() {
		return userDataMgr.getUserName();
	}

	public String getHeadImage() {
		return userDataMgr.getHeadImage();
	}

	public String getHeadFrame() {
		return userGameDataMgr.getHeadBox();
	}

	public int getCareer() {
		return getMainRoleHero().getCareerType();
	}

	public String getTemplateId() {
		// return
		// getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getTemplateId();
		return getMainRoleHero().getTemplateId();

	}

	public void SetCareer(int career) {
		FSHeroBaseInfoMgr.getInstance().setCareerType(getMainRoleHero(), career);
		RankingMgr.getInstance().onPlayerChange(this);
		getFriendMgr().onPlayerChange(this);
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
		// return userGameDataMgr.getUserId();
		return this.userId;
	}

	public TableUserOtherIF getTableUserOther() {
		return userGameDataMgr.getReadOnly();
	}

	public void setTemplateId(String templateId) {
		if (templateId != null) {
			// getMainRoleHero().getRoleBaseInfoMgr().setTemplateId(templateId);
			FSHeroBaseInfoMgr.getInstance().setTemplateId(getMainRoleHero(), templateId);

			// 通知一下监听的人，修改对应数据
			Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
			if (observer != null) {
				observer.playerChangeTemplateId(this);
			}
		}
	}

	public void SetModelId(int modelId) {
		if (modelId > 0) {
			// getMainRoleHero().getRoleBaseInfoMgr().setModelId(modelId);
			FSHeroBaseInfoMgr.getInstance().setModelId(getMainRoleHero(), modelId);
			RankingMgr.getInstance().onPlayerChange(this);
		}
	}

	public int getModelId() {
		// return
		// getMainRoleHero().getRoleBaseInfoMgr().getBaseInfo().getModeId();
		return getMainRoleHero().getModeId();
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

	// public ItemBagMgr getItemBagMgr() {
	// return itemBagMgr;
	// }

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

	public RedPointMgr getRedPointMgr() {
		return redPointMgr;
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

	public UpgradeMgr getUpgradeMgr() {
		return upgradeMgr;
	}

	public PlayerQuestionMgr getPlayerQuestionMgr() {
		return playerQuestionMgr;
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
	 * 获取个人的帮派副本数据
	 * 
	 * @return
	 */
	public UserGroupCopyMapRecordMgr getUserGroupCopyRecordMgr() {
		return userGroupCopyRecordMgr;
	}

	/**
	 * 获取个人法宝神器羁绊管理类
	 * 
	 * @return
	 */
	public MagicEquipFetterMgr getMe_FetterMgr() {
		return me_FetterMgr;
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
	 * 是否是机器人
	 * 
	 * @return
	 */
	public boolean isRobot() {
		return userDataMgr.getUser().isRobot();
	}

	/** 每分钟执行 */
	public void onSecond() {
		if (oneSecondTimeAction == null) {
			return;
		}

		oneSecondTimeAction.doAction();
	}

	/**
	 * 获取体力信息
	 * 
	 * @return
	 */
	public PowerInfo getPowerInfo() {
		return powerInfo;
	}

	public IPrivilegeManager getPrivilegeMgr() {
		return privilegeMgr;
	}

	public ITaoistMgr getTaoistMgr() {
		return taoistMgr;
	}

	/**
	 * 通过英雄的ModelId获取英雄的羁绊
	 *
	 * @param modelId
	 * @return
	 */
	public SynFettersData getHeroFettersByModelId(int modelId) {
		HeroFettersData data = heroFettersMgr.get(userId);
		if (data == null) {
			return null;
		}

		return data.getHeroFettersByModelId(modelId);
	}

	/**
	 * 获取所有的英雄羁绊
	 *
	 * @return
	 */
	public List<SynFettersData> getAllHeroFetters() {
		HeroFettersData data = heroFettersMgr.get(userId);
		if (data == null) {
			return null;
		}

		return data.getAllHeroFetters();
	}

	/**
	 * 增加英雄羁绊数据
	 *
	 * @param heroModelId
	 * @param fettersData
	 * @param canSyn 是否可以同步数据
	 */
	public void addOrUpdateHeroFetters(int heroModelId, SynFettersData fettersData, boolean canSyn) {
		HeroFettersData data = heroFettersMgr.get(userId);
		if (data == null) {
			return;
		}

		data.addOrUpdateHeroFetters(this, heroModelId, fettersData, canSyn);
	}

	//
	// /**
	// * 检查所有英雄的羁绊
	// */
	// private void checkAllHeroFetters() {
	// Enumeration<? extends Hero> herosEnumeration =
	// getHeroMgr().getHerosEnumeration(this);
	// while (herosEnumeration.hasMoreElements()) {
	// Hero hero = herosEnumeration.nextElement();
	// if (hero == null) {
	// continue;
	// }
	//
	// FettersBM.checkOrUpdateHeroFetters(this, hero.getModeId(), false);
	// }
	// }

	public UserTmpGameDataFlag getUserTmpGameDataFlag() {
		return userTmpGameDataFlag;
	}

	private IPrivilegeProvider monthProvider;

	public IPrivilegeProvider getMonthCardPrivilegeProvider() {
		if (monthProvider == null) {
			monthProvider = MonthCardPrivilegeMgr.CreateProvider(this);
		}
		return monthProvider;
	}

	/**
	 * 获取秘境基础数据的Holder
	 * 
	 * @return
	 */
	public GroupSecretBaseInfoSynDataHolder getBaseHolder() {
		return baseHolder;
	}

	/**
	 * 获取秘境Team信息的Holder
	 * 
	 * @return
	 */
	public GroupSecretTeamInfoSynDataHolder getTeamHolder() {
		return teamHolder;
	}

	/**
	 * 获取上次发送组队信息的时间
	 * 
	 * @return
	 */
	public long getLastTeamChatCahceTime() {
		return lastTeamChatCahceTime;
	}

	/**
	 * 设置上次发送组队信息的缓存时间
	 * 
	 * @param lastTeamChatCahceTime
	 */
	public void setLastTeamChatCahceTime(long lastTeamChatCahceTime) {
		this.lastTeamChatCahceTime = lastTeamChatCahceTime;
	}

	@Override
	public long getLastLoginTime() {
		return UserDataDao.getInstance().getByUserId(userId).getLastLoginTime();
	}

	/**
	 * 增加数据
	 * 
	 * @param modelId
	 * @param count
	 * @return
	 */
	public ItemData newItemData(int modelId, int count) {
		ItemData itemData = new ItemData();
		if (itemData.init(modelId, count)) {
			String slotId = generateSlotId(userId);
			itemData.setId(slotId);// 设置物品Id
			itemData.setUserId(userId);// 设置角色Id
		}
		return itemData;
	}

	/**
	 * 生成背包中物品的格子Id
	 * 
	 * @return
	 */
	private String generateSlotId(String userId) {
		long newId = itemGenerateId.incrementAndGet();
		StringBuilder sb = new StringBuilder();
		return sb.append(userId).append("_").append(newId).toString();
	}
}