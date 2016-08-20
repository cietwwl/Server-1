package com.playerdata.activity.rankType.cfg;

public class SendRewardRecord {
	private String id;
	private String version;
	private boolean isSend;
	private String enumId;
	
	
	/**生成奖励的时间*/
	private long lasttime;
	
	
	public String getEnumId() {
		return enumId;
	}
	public void setEnumId(String enumId) {
		this.enumId = enumId;
	}
	public boolean isSend() {
		return isSend;
	}
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public long getLasttime() {
		return lasttime;
	}
	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}
	
	
	
	
	
	
}
