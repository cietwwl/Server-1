package com.rw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.account.ServerInfo;
import com.rw.actionHelper.ActionEnum;
import com.rw.actionHelper.ActionRateHelper;
import com.rw.dataSyn.JsonUtil;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.GroupCopy.data.GroupCopyDataHolder;
import com.rw.handler.GroupCopy.data.GroupCopyUserDataHolder;
import com.rw.handler.activity.ActivityCountHolder;
import com.rw.handler.activity.daily.ActivityDailyCountHolder;
import com.rw.handler.battletower.data.BattleTowerData;
import com.rw.handler.chat.data.ChatData;
import com.rw.handler.copy.data.CopyHolder;
import com.rw.handler.daily.DailyActivityDataHolder;
import com.rw.handler.equip.HeroEquipHolder;
import com.rw.handler.fixEquip.FixNormEquipDataItemHolder;
import com.rw.handler.fixExpEquip.FixExpEquipDataItemHolder;
import com.rw.handler.fresheractivity.FresherActivityHolder;
import com.rw.handler.group.data.GroupDataVersion;
import com.rw.handler.group.data.GroupRequestCacheData;
import com.rw.handler.group.holder.GroupApplyMemberHolder;
import com.rw.handler.group.holder.GroupBaseDataHolder;
import com.rw.handler.group.holder.GroupLogHolder;
import com.rw.handler.group.holder.GroupNormalMemberHolder;
import com.rw.handler.group.holder.GroupResearchSkillDataHolder;
import com.rw.handler.group.holder.UserGroupDataHolder;
import com.rw.handler.groupCompetition.data.baseinfo.GCompBaseInfoHolder;
import com.rw.handler.groupCompetition.data.battle.GCompMatchBattleSynDataHolder;
import com.rw.handler.groupCompetition.data.events.GCompEventsDataHolder;
import com.rw.handler.groupCompetition.data.guess.GCQuizEventItemHolder;
import com.rw.handler.groupCompetition.data.guess.GCompUserQuizItemHolder;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMemberHolder;
import com.rw.handler.groupCompetition.data.prepare.SameSceneSynDataHolder;
import com.rw.handler.groupCompetition.data.team.GCompTeamHolder;
import com.rw.handler.groupFight.data.GFightOnlineGroupHolder;
import com.rw.handler.groupFight.data.GFightOnlineResourceHolder;
import com.rw.handler.groupFight.data.UserGFightOnlineHolder;
import com.rw.handler.groupsecret.GroupSecretBaseInfoSynDataHolder;
import com.rw.handler.groupsecret.GroupSecretTeamDataHolder;
import com.rw.handler.groupsecret.GroupSecretUserInfoSynDataHolder;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rw.handler.itembag.ItembagHolder;
import com.rw.handler.magicSecret.MagicChapterInfoHolder;
import com.rw.handler.magicSecret.MagicSecretHolder;
import com.rw.handler.majordata.MajorDataholder;
import com.rw.handler.peakArena.PeakArenaDataHolder;
import com.rw.handler.player.RoleBaseInfoHolder;
import com.rw.handler.player.UserGameDataHolder;
import com.rw.handler.sign.SignDataHolder;
import com.rw.handler.store.StoreItemHolder;
import com.rw.handler.taoist.TaoistDataHolder;
import com.rw.handler.task.TaskItemHolder;
import com.rw.handler.teamBattle.data.TBTeamItemHolder;
import com.rw.handler.teamBattle.data.UserTeamBattleDataHolder;
import com.rw.handler.worldboss.data.WBData;
import com.rw.handler.worldboss.data.WBDataHolder;

/*
 * 角色信息
 * @author HC
 * @date 2015年12月15日 下午8:36:05
 * @Description 
 */
public class Client {
	
	private ActionRateHelper rateHelper = new ActionRateHelper();
	// private static Random r = new Random();// 随机性别
	private String accountId;// 帐号Id
	private String password = "123456";// 帐号密码
	private String userId;// 游戏用户Id
	private String token;// token
	private int serverId;// 上一次登录的服务器Id
	// private String lastHost;// 上一次登录的服务器IP
	// private String lastPort;// 上一次登录的服务器端口
	private List<ServerInfo> serverList = new ArrayList<ServerInfo>();// 服务器列表信息

	private ClientMsgHandler msgHandler;
	private StoreItemHolder storeItemHolder = new StoreItemHolder();
	private ItembagHolder itembagHolder = new ItembagHolder();
	private TaskItemHolder taskItemHolder = new TaskItemHolder();
	// 帮派的Holder域
	private UserGroupDataHolder userGroupDataHolder = new UserGroupDataHolder();
	private GroupNormalMemberHolder normalMemberHolder = new GroupNormalMemberHolder();
	private GroupApplyMemberHolder applyMemberHolder = new GroupApplyMemberHolder();
	private GroupLogHolder logHolder = new GroupLogHolder();
	private GroupResearchSkillDataHolder researchSkillDataHolder = new GroupResearchSkillDataHolder();
	private GroupBaseDataHolder groupBaseDataHolder = new GroupBaseDataHolder();
	private GroupDataVersion groupVersion = new GroupDataVersion();
	private GroupRequestCacheData groupCacheData = new GroupRequestCacheData();

	// 帮派副本数据
	private GroupCopyDataHolder groupCopyHolder = new GroupCopyDataHolder();
	// 帮派副本个人数据，主要保存副本个人战斗次数
	private GroupCopyUserDataHolder userGroupCopyDataHolder = new GroupCopyUserDataHolder();

	// 封神台的数据
	private BattleTowerData battleTowerData = new BattleTowerData();
	// 英雄的装备数据
	private HeroEquipHolder heroEquipHolder = new HeroEquipHolder();

	// 签到数据
	private SignDataHolder signDataHolder = new SignDataHolder();
	// 日常数据
	private DailyActivityDataHolder dailyActivityDataHolder = new DailyActivityDataHolder();

	// 玩家通用活动一数据
	private ActivityCountHolder activityCountHolder = new ActivityCountHolder();
	// 玩家通用活动二数据
	private ActivityDailyCountHolder activityDailyCountHolder = new ActivityDailyCountHolder();

	// 玩家封神之路数据
	private FresherActivityHolder fresherActivityHolder = new FresherActivityHolder();

	// 神器
	private FixNormEquipDataItemHolder fixNormEquipDataItemHolder = new FixNormEquipDataItemHolder();
	private FixExpEquipDataItemHolder fixExpEquipDataItemHolder = new FixExpEquipDataItemHolder();

	private GroupSecretTeamDataHolder groupSecretTeamDataHolder = new GroupSecretTeamDataHolder();
	private UserHerosDataHolder userHerosDataHolder = new UserHerosDataHolder();
	private GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = new GroupSecretBaseInfoSynDataHolder();
	private GroupSecretUserInfoSynDataHolder groupSecretUserInfoSynDataHolder = new GroupSecretUserInfoSynDataHolder();

	// private GroupSecretInviteDataHolder groupSecretInviteDataHolder = new GroupSecretInviteDataHolder();
	// 乾坤幻境
	private MagicSecretHolder magicSecretHolder = new MagicSecretHolder();
	private MagicChapterInfoHolder magicChapterInfoHolder = new MagicChapterInfoHolder();

	// 在线帮战
	private UserGFightOnlineHolder ugfHolder = new UserGFightOnlineHolder();
	private GFightOnlineResourceHolder gfResHolder = new GFightOnlineResourceHolder();
	private GFightOnlineGroupHolder gfGroupHolder = new GFightOnlineGroupHolder();

	// 组队战
	private TBTeamItemHolder tbTeamItemHolder = new TBTeamItemHolder(rateHelper);
	private UserTeamBattleDataHolder utbDataHolder = new UserTeamBattleDataHolder();

	// 争霸赛
	private GCompUserQuizItemHolder userQuizItemHolder = new GCompUserQuizItemHolder();
	private GCQuizEventItemHolder quizEventItemHolder = new GCQuizEventItemHolder();
	private SameSceneSynDataHolder prepareAreaHolder = new SameSceneSynDataHolder();
	private GCompBaseInfoHolder gCompBaseInfoHolder = new GCompBaseInfoHolder();
	private GCompTeamHolder gCompTeamHolder = new GCompTeamHolder();
	private GCompOnlineMemberHolder gCompOnlinememberHolder = new GCompOnlineMemberHolder();
	private GCompEventsDataHolder gCompEventsDataHolder = new GCompEventsDataHolder();
	private GCompMatchBattleSynDataHolder gCompMatchBattleSynDataHolder = new GCompMatchBattleSynDataHolder();

	// 主要数据
	private MajorDataholder majorDataholder = new MajorDataholder();

	private CopyHolder copyHolder = new CopyHolder();

	private TaoistDataHolder taoistDataHolder = new TaoistDataHolder();

	private UserGameDataHolder userGameDataHolder = new UserGameDataHolder();
	
	private RoleBaseInfoHolder roleBaseInfoHolder = new RoleBaseInfoHolder();

	private PeakArenaDataHolder peakArenaDataHolder = new PeakArenaDataHolder();
	
	//世界boss
	private WBDataHolder wbDataHolder = new WBDataHolder();
	
	
	
	// last seqId
	// private volatile int lastSeqId;
	private volatile CommandInfo commandInfo = new CommandInfo(null, 0);

	private AtomicBoolean closeFlat = new AtomicBoolean();
	
	private Queue<AsynExecuteTask> asynExecuteResps = new ConcurrentLinkedQueue<AsynExecuteTask>();

	// 聊天数据缓存
	private ChatData chatData = new ChatData();

	public Client(String accountIdP) {
		this.accountId = accountIdP;

		final Client thisClient = this;
		msgHandler = new ClientMsgHandler() {
			@Override
			public Client getClient() {
				return thisClient;
			}
		};
	}

	/**
	 * 连接新的数据
	 * 
	 * @param host
	 * @param port
	 */
	public boolean doConnect(final String host, final int port) {

		return ChannelServer.getInstance().doConnect(this, host, port);

	}

	public void closeConnect() {
		ChannelServer.getInstance().remove(this);
	}

	public StoreItemHolder getStoreItemHolder() {
		return storeItemHolder;
	}

	public ItembagHolder getItembagHolder() {
		return itembagHolder;
	}

	public ClientMsgHandler getMsgHandler() {
		return msgHandler;
	}

	public TaskItemHolder getTaskItemHolder() {
		return taskItemHolder;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getServerId() {
		return serverId;
	}

	public void setLastServerId(int serverId) {
		this.serverId = serverId;
	}

	public ServerInfo getServerById(int serverId) {
		ServerInfo target = null;
		for (ServerInfo serverInfo : serverList) {
			if (serverInfo.getZoneId() == serverId) {
				target = serverInfo;
				break;
			}
		}
		return target;
	}

	public List<ServerInfo> getServerList() {
		return serverList;
	}

	public void setServerList(List<ServerInfo> serverList) {
		for (ServerInfo serverInfo : serverList) {
			boolean blnAdd = true;
			for (ServerInfo si : serverList) {
				if (si.getServerIP() == serverInfo.getServerIP() && si.getServerPort() == serverInfo.getServerPort() && si.getZoneId() == serverInfo.getZoneId()) {
					si.setHasRole(serverInfo.isHasRole());
					blnAdd = false;
					break;
				}
			}
			if (blnAdd) {
				this.serverList.add(serverInfo);
			}
		}
		this.serverList = serverList;
	}

	public void addServerInfo(ServerInfo serverInfo) {
		for (ServerInfo si : serverList) {
			if (si.getServerIP() == serverInfo.getServerIP() && si.getServerPort() == serverInfo.getServerPort() && si.getZoneId() == serverInfo.getZoneId()) {
				return;
			}
		}
		this.serverList.add(serverInfo);
	}
	
	public void addAsynExecuteResp(AsynExecuteTask task) {
		synchronized (this.asynExecuteResps) {
			this.asynExecuteResps.add(task);
		}
	}
	
	public void executeAsynResp() {
		Queue<AsynExecuteTask> queue = null;
		if (this.asynExecuteResps.size() > 0) {
			queue = new LinkedList<AsynExecuteTask>(this.asynExecuteResps);
			this.asynExecuteResps.clear();
		}
		if (queue != null) {
			for (AsynExecuteTask task : queue) {
				task.executeResp(this);
			}
		}
	}

	public GroupNormalMemberHolder getNormalMemberHolder() {
		return normalMemberHolder;
	}

	public GroupApplyMemberHolder getApplyMemberHolder() {
		return applyMemberHolder;
	}

	public GroupLogHolder getLogHolder() {
		return logHolder;
	}

	public GroupResearchSkillDataHolder getResearchSkillDataHolder() {
		return researchSkillDataHolder;
	}

	public GroupBaseDataHolder getGroupBaseDataHolder() {
		return groupBaseDataHolder;
	}

	public UserGroupDataHolder getUserGroupDataHolder() {
		return userGroupDataHolder;
	}

	public GroupRequestCacheData getGroupCacheData() {
		return groupCacheData;
	}

	public BattleTowerData getBattleTowerData() {
		return battleTowerData;
	}

	public GroupCopyDataHolder getGroupCopyHolder() {
		return groupCopyHolder;
	}

	public GroupCopyUserDataHolder getGroupCopyUserData() {
		return userGroupCopyDataHolder;
	}

	public FresherActivityHolder getFresherActivityHolder() {
		return fresherActivityHolder;
	}

	public void setFresherActivityHolder(FresherActivityHolder fresherActivityHolder) {
		this.fresherActivityHolder = fresherActivityHolder;
	}

	public FixNormEquipDataItemHolder getFixNormEquipDataItemHolder() {
		return fixNormEquipDataItemHolder;
	}

	public void setFixNormEquipDataItemHolder(FixNormEquipDataItemHolder fixNormEquipDataItemHolder) {
		this.fixNormEquipDataItemHolder = fixNormEquipDataItemHolder;
	}

	public FixExpEquipDataItemHolder getFixExpEquipDataItemHolder() {
		return fixExpEquipDataItemHolder;
	}

	public void setFixExpEquipDataItemHolder(FixExpEquipDataItemHolder fixExpEquipDataItemHolder) {
		this.fixExpEquipDataItemHolder = fixExpEquipDataItemHolder;
	}

	public MagicSecretHolder getMagicSecretHolder() {
		return magicSecretHolder;
	}

	public void setMagicSecretHolder(MagicSecretHolder magicSecretHolder) {
		this.magicSecretHolder = magicSecretHolder;
	}

	public HeroEquipHolder getHeroEquipHolder() {
		return heroEquipHolder;
	}

	public ActivityCountHolder getActivityCountHolder() {
		return activityCountHolder;
	}

	public ActivityDailyCountHolder getActivityDailyCountHolder() {
		return activityDailyCountHolder;
	}

	public SignDataHolder getSignDataHolder() {
		return signDataHolder;
	}

	public void setSignDataHolder(SignDataHolder signDataHolder) {
		this.signDataHolder = signDataHolder;
	}

	public DailyActivityDataHolder getDailyActivityDataHolder() {
		return dailyActivityDataHolder;
	}

	public void setDailyActivityDataHolder(DailyActivityDataHolder dailyActivityDataHolder) {
		this.dailyActivityDataHolder = dailyActivityDataHolder;
	}

	public GroupSecretTeamDataHolder getGroupSecretTeamDataHolder() {
		return groupSecretTeamDataHolder;
	}

	public UserHerosDataHolder getUserHerosDataHolder() {
		return userHerosDataHolder;
	}

	public MagicChapterInfoHolder getMagicChapterInfoHolder() {
		return magicChapterInfoHolder;
	}

	public void setUserGameDataHolder(UserGameDataHolder userGameDataHolder) {
		this.userGameDataHolder = userGameDataHolder;
	}

	public UserGameDataHolder getUserGameDataHolder() {
		return userGameDataHolder;
	}

	public RoleBaseInfoHolder getRoleBaseInfoHolder() {
		return roleBaseInfoHolder;
	}

	public CopyHolder getCopyHolder() {
		return copyHolder;
	}

	public void setMagicChapterInfoHolder(MagicChapterInfoHolder magicChapterInfoHolder) {
		this.magicChapterInfoHolder = magicChapterInfoHolder;
	}

	public String getGroupVersion() {
		groupVersion.setApplyMemberData(applyMemberHolder.getVersion());
		groupVersion.setGroupMemberData(normalMemberHolder.getVersion());
		groupVersion.setGroupBaseData(groupBaseDataHolder.getVersion());
		groupVersion.setResearchSkill(researchSkillDataHolder.getVersion());
		return JsonUtil.writeValue(groupVersion);
	}

	public GroupSecretBaseInfoSynDataHolder getGroupSecretBaseInfoSynDataHolder() {
		return groupSecretBaseInfoSynDataHolder;
	}

	public GroupSecretUserInfoSynDataHolder getGroupSecretUserInfoSynDataHolder() {
		return groupSecretUserInfoSynDataHolder;
	}

	// public GroupSecretInviteDataHolder getGroupSecretInviteDataHolder() {
	// return groupSecretInviteDataHolder;
	// }

	public MajorDataholder getMajorDataholder() {
		return majorDataholder;
	}

	public void setMajorDataholder(MajorDataholder majorDataholder) {
		this.majorDataholder = majorDataholder;
	}

	public AtomicBoolean getCloseFlat() {
		return this.closeFlat;
	}

	public CommandInfo getCommandInfo() {
		return commandInfo;
	}

	public void setCommandInfo(CommandInfo commandInfo) {
		this.commandInfo = commandInfo;
	}

	public TaoistDataHolder getTaoistDataHolder() {
		return taoistDataHolder;
	}

	public void setTaoistDataHolder(TaoistDataHolder taoistDataHolder) {
		this.taoistDataHolder = taoistDataHolder;
	}

	public UserGFightOnlineHolder getUserGFightOnlineHolder() {
		return ugfHolder;
	}

	public GFightOnlineResourceHolder getGFightOnlineResourceHolder() {
		return gfResHolder;
	}

	public GFightOnlineGroupHolder getGFightOnlineGroupHolder() {
		return gfGroupHolder;
	}

	public TBTeamItemHolder getTBTeamItemHolder() {
		return tbTeamItemHolder;
	}

	public UserTeamBattleDataHolder getUserTeamBattleDataHolder() {
		return utbDataHolder;
	}

	public GCompUserQuizItemHolder getUserQuizItemHolder() {
		return userQuizItemHolder;
	}

	public GCQuizEventItemHolder getQuizEventItemHolder() {
		return quizEventItemHolder;
	}

	public SameSceneSynDataHolder getSameSceneSynDataHolder() {
		return prepareAreaHolder;
	}

	public ChatData getChatData() {
		return chatData;
	}

	public PeakArenaDataHolder getPeakArenaDataHolder() {
		return peakArenaDataHolder;
	}

	public void setPeakArenaDataHolder(PeakArenaDataHolder peakArenaDataHolder) {
		this.peakArenaDataHolder = peakArenaDataHolder;
	}

	public GCompBaseInfoHolder getGCompBaseInfoHolder() {
		return gCompBaseInfoHolder;
	}

	public GCompTeamHolder getGCompTeamHolder() {
		return gCompTeamHolder;
	}

	public GCompOnlineMemberHolder getGCompOnlinememberHolder() {
		return gCompOnlinememberHolder;
	}

	public GCompEventsDataHolder getGCompEventsDataHolder() {
		return gCompEventsDataHolder;
	}

	public GCompMatchBattleSynDataHolder getgCompMatchBattleSynDataHolder() {
		return gCompMatchBattleSynDataHolder;
	}
	
	
	
	
	public WBDataHolder getWbDataHolder() {
		return wbDataHolder;
	}

	/**
	 * 
	 * @return
	 */
	public ActionRateHelper getRateHelper(){
		return rateHelper;
	}
	
	public RandomMethodIF getNextModuleHandler(){
		ActionEnum act = rateHelper.getRandomAction();
		if(null == act) return null;
		return rateHelper.getRandomAction().getExeHandler();
	}
}