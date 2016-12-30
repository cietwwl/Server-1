package com.rwbase.dao.activityTime;

public class ActivitySpecialTimeCfg{
	
	private int id; //活动id
	private String zoneId;	//活动的标题
	private int cfgId; //开始时间
	private String startTime; //结束时间
	private String endTime; //是否隔天自动刷新
	private String startViceTime; //开启等级
	private String endViceTime; //活动版本
	private String rangeTime;	//活动的开启时间
	private String actDesc;	//活动的结束时间
	private int version;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getZoneId() {
		return zoneId;
	}
	
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	
	public int getCfgId() {
		return cfgId;
	}
	
	public void setCfgId(int cfgId) {
		this.cfgId = cfgId;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getStartViceTime() {
		return startViceTime;
	}
	
	public void setStartViceTime(String startViceTime) {
		this.startViceTime = startViceTime;
	}
	
	public String getEndViceTime() {
		return endViceTime;
	}
	
	public void setEndViceTime(String endViceTime) {
		this.endViceTime = endViceTime;
	}
	
	public String getRangeTime() {
		return rangeTime;
	}
	
	public void setRangeTime(String rangeTime) {
		this.rangeTime = rangeTime;
	}
	
	public String getActDesc() {
		return actDesc;
	}
	
	public void setActDesc(String actDesc) {
		this.actDesc = actDesc;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
