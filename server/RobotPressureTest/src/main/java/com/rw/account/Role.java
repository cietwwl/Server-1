package com.rw.account;

import io.netty.channel.Channel;

/*
 * 角色信息
 * @author HC
 * @date 2015年12月14日 下午3:22:19
 * @Description 
 */
public class Role {
	private String roleId;// 角色Id
	private String token;// token
	private String accountId;// 账户Id
	private String password;// 密码
	private String connectHost;// 连接的服务器IP
	private int connectPort;// 连接的服务器端口
	private Channel channel;// 当前通信的Channel

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getConnectHost() {
		return connectHost;
	}

	public void setConnectHost(String connectHost) {
		this.connectHost = connectHost;
	}

	public int getConnectPort() {
		return connectPort;
	}

	public void setConnectPort(int connectPort) {
		this.connectPort = connectPort;
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

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}