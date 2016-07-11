package com.rw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.account.ServerInfo;
import com.rw.dataSyn.JsonUtil;
import com.rw.handler.activity.ActivityCountHolder;
import com.rw.handler.activity.daily.ActivityDailyCountHolder;
import com.rw.handler.battletower.data.BattleTowerData;
import com.rw.handler.copy.CopyHolder;
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
import com.rw.handler.groupsecret.GroupSecretBaseInfoSynDataHolder;
import com.rw.handler.groupsecret.GroupSecretInviteDataHolder;
import com.rw.handler.groupsecret.GroupSecretTeamDataHolder;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rw.handler.itembag.ItembagHolder;
import com.rw.handler.magicSecret.MagicChapterInfoHolder;
import com.rw.handler.magicSecret.MagicSecretHolder;
import com.rw.handler.majordata.MajorDataholder;
import com.rw.handler.sign.SignDataHolder;
import com.rw.handler.store.StoreItemHolder;
import com.rw.handler.taoist.TaoistDataHolder;
import com.rw.handler.task.TaskItemHolder;

/*
 * 角色信息
 * @author HC
 * @date 2015年12月15日 下午8:36:05
 * @Description 
 */
public class Client {

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
	private GroupSecretInviteDataHolder groupSecretInviteDataHolder = new GroupSecretInviteDataHolder();
	// 乾坤幻境
	private MagicSecretHolder magicSecretHolder = new MagicSecretHolder();
	private MagicChapterInfoHolder magicChapterInfoHolder = new MagicChapterInfoHolder();

	// 主要数据
	private MajorDataholder majorDataholder = new MajorDataholder();
	
	private CopyHolder copyHolder = new CopyHolder();
	
	private TaoistDataHolder taoistDataHolder = new TaoistDataHolder();

	// last seqId
	// private volatile int lastSeqId;
	private volatile CommandInfo commandInfo = new CommandInfo(null, 0);

	private AtomicBoolean closeFlat = new AtomicBoolean();

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
				if(si.getServerIP() == serverInfo.getServerIP() && si.getServerPort() == serverInfo.getServerPort() && si.getZoneId() == serverInfo.getZoneId()){
					si.setHasRole(serverInfo.isHasRole());
					blnAdd = false;
					break;
				}
			}
			if(blnAdd){
				this.serverList.add(serverInfo);
			}
		}
		this.serverList = serverList;
	}
	
	public void addServerInfo(ServerInfo serverInfo){
		for (ServerInfo si : serverList) {
			if(si.getServerIP() == serverInfo.getServerIP() && si.getServerPort() == serverInfo.getServerPort() && si.getZoneId() == serverInfo.getZoneId()){
				return;
			}
		}
		this.serverList.add(serverInfo);
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

	public GroupSecretInviteDataHolder getGroupSecretInviteDataHolder() {
		return groupSecretInviteDataHolder;
	}

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

}