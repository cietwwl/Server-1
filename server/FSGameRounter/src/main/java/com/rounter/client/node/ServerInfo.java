package com.rounter.client.node;


/**
 * 区服信息
 * @author Alex
 * 2016年12月16日 下午12:13:36
 */
public class ServerInfo implements Comparable<ServerInfo>{
	
	private String ip;
	
	private String id;
	
	private String name;
	
	private int port;
	
	private boolean isActive = true;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(ServerInfo o) {
		return Integer.valueOf(id) - Integer.valueOf(o.id);
	}
}
