package com.playerdata.activity.VitalityType.cfg;




public class ActivityVitalityCfg {

	private String id;
	
	private String title;
	
	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	// 活跃度能否领奖，0=可以，1=不可以
	private boolean isCanGetReward;
	
	private String enumID;
	


	private String version;

	private int levelLimit;

	


	public String getEnumID() {
		return enumID;
	}

	public void setEnumID(String enumID) {
		this.enumID = enumID;
	}

	public boolean isCanGetReward() {
		return isCanGetReward;
	}

	public void setCanGetReward(boolean isCanGetReward) {
		this.isCanGetReward = isCanGetReward;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getTitle() {
		return title;
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

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	






	
	
}
