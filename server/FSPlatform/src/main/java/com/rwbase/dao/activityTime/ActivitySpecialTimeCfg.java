package com.rwbase.dao.activityTime;

import com.rw.service.http.platformResponse.ServerType;

public class ActivitySpecialTimeCfg{
	
	private int id; //活动id
	private ServerType serType;	//服务器类型
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
	
	public ServerType getSerType() {
		return serType;
	}
	
	public String getZoneId() {
		return zoneId;
	}
	
	public int getCfgId() {
		return cfgId;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public String getStartViceTime() {
		return startViceTime;
	}
	
	public String getEndViceTime() {
		return endViceTime;
	}
	
	public String getRangeTime() {
		return rangeTime;
	}
	
	public String getActDesc() {
		return actDesc;
	}
	
	public int getVersion() {
		return version;
	}
}
