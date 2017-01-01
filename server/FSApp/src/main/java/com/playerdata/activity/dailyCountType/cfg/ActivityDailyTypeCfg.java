package com.playerdata.activity.dailyCountType.cfg;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityDailyTypeCfg implements ActivityCfgIF{

	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private int version;
	
	private int levelLimit;
	
	private String enumId;
	
	public String getEnumId() {
		return enumId;
	}

	public void setEnumId(String enumId) {
		this.enumId = enumId;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	private int sortNum;
	
	private String countLimit;


	public int getId() {
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

	@Override
	public int getCfgId() {
		return 0;
	}

	@Override
	public int getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getVipLimit() {
		return 0;
	}

	@Override
	public boolean isDailyRefresh() {
		return true;
	}

	@Override
	public void setStartTime(String startTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndTime(String endTime) {
		// TODO Auto-generated method stub
		
	}
}
