package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.List;



public class ActivityDailyCountTypeCfg {

	private String id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	private int sortNum;
	
	private String countLimit;


	public String getId() {
		return id;
	}

	public long getEndTime() {
		return endTime;
	}

	
	public int getSortNum() {
		return sortNum;
	}
	
	public String getCountLimit() {
		return countLimit;
	}

	public void setCountLimit(String countLimit) {
		this.countLimit = countLimit;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}









	
	
}
