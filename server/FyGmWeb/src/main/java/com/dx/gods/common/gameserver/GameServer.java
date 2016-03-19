package com.dx.gods.common.gameserver;

public class GameServer {
	private int id;
	private String serverIp;
	private int port;
	private String httpUrl;
	private int httpPort;
	private String serverName;
	private boolean islinux;
	private String versionId;
	
	public GameServer(int id, String serverIp, int port, String httpUrl, int httpPort, String serverName, boolean islinux, String versionId){
		this.id = id;
		this.serverIp = serverIp;
		this.port = port;
		this.httpUrl = httpUrl;
		this.httpPort = httpPort;
		this.serverName = serverName;
		this.islinux = islinux;
		this.versionId = versionId;
		
	}
	
	public GameServer(int id, String serverName){
		this.id = id;
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public int getPort() {
		return port;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public boolean isIslinux() {
		return islinux;
	}

	public int getId() {
		return id;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
}
