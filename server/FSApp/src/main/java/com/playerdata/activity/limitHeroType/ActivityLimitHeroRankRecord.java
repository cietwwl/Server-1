package com.playerdata.activity.limitHeroType;

public class ActivityLimitHeroRankRecord {
	private int integral;
	private String uid;
	private String playerName;
	private long regditTime;//存于数据库，不发给客户端
	private String version;//存于数据库，不发给客户端
	
	
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public long getRegditTime() {
		return regditTime;
	}
	public void setRegditTime(long regditTime) {
		this.regditTime = regditTime;
	}
	
	
	public int getIntegral() {
		return integral;
	}
	public void setIntegral(int integral) {
		this.integral = integral;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	
	
}
