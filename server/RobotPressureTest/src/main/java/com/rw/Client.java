package com.rw;

import java.util.List;

import com.rw.account.ServerInfo;
import com.rw.dataSyn.JsonUtil;
import com.rw.handler.battletower.data.BattleTowerData;
import com.rw.handler.group.data.GroupDataVersion;
import com.rw.handler.group.data.GroupRequestCacheData;
import com.rw.handler.group.holder.GroupApplyMemberHolder;
import com.rw.handler.group.holder.GroupBaseDataHolder;
import com.rw.handler.group.holder.GroupLogHolder;
import com.rw.handler.group.holder.GroupNormalMemberHolder;
import com.rw.handler.group.holder.GroupResearchSkillDataHolder;
import com.rw.handler.group.holder.UserGroupDataHolder;
import com.rw.handler.itembag.ItembagHolder;
import com.rw.handler.store.StoreItemHolder;
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
	private List<ServerInfo> serverList;// 服务器列表信息

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

	// public String getLastHost() {
	// return lastHost;
	// }

	// public void setLastHost(String lastHost) {
	// this.lastHost = lastHost;
	// }

	// public int getLastPort() {
	// return Integer.parseInt(lastPort);
	// }
	//
	// public void setLastPort(String lastPort) {
	// this.lastPort = lastPort;
	// }

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
		this.serverList = serverList;
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

	public String getGroupVersion() {
		groupVersion.setApplyMemberData(applyMemberHolder.getVersion());
		groupVersion.setGroupMemberData(normalMemberHolder.getVersion());
		groupVersion.setGroupBaseData(groupBaseDataHolder.getVersion());
		groupVersion.setResearchSkill(researchSkillDataHolder.getVersion());
		return JsonUtil.writeValue(groupVersion);
	}
}