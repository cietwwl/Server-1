package com.rounter.param.impl;

import com.rounter.param.IRequestData;

public class ReqRoleInfo implements IRequestData{

	private long requestID;
	
	private String accountId;
	
	private int gameId;
	
	private int platform;
	
	private String serverId;
	
	@Override
	public long getId() {
		return requestID;
	}

	
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}



	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	

}
