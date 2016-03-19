package com.dx.gods.common.gameserver;

public class GSService {
	private String address;
	private int port;
	
	public GSService(String address, int port){
		this.address = address;
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
