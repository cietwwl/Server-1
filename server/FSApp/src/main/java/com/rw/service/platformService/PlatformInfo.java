package com.rw.service.platformService;

public class PlatformInfo {
	private String ip;
	private int port;
	
	public PlatformInfo(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	
}
