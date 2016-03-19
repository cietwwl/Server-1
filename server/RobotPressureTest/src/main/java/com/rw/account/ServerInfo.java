package com.rw.account;

/*
 * 对于个人来说，每个服中的角色信息列表
 * @author HC
 * @date 2015年12月15日 下午4:52:02
 * @Description 
 */
public class ServerInfo {
	private int zoneId;// 服务器的Id
	private String serverIP;// 服务器的IP
	private String serverPort;// 端口
	private boolean hasRole;// 是否有角色


	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return Integer.parseInt(serverPort);
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public boolean isHasRole() {
		return hasRole;
	}

	public void setHasRole(boolean hasRole) {
		this.hasRole = hasRole;
	}
}