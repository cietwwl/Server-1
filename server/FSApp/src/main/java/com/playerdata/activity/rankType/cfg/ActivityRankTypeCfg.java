package com.playerdata.activity.rankType.cfg;


public class ActivityRankTypeCfg {

	private String id;

	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	private int levelLimit;

	private String version;
	
	private int dailyOrRealtime;
	
	private String rankRange;
	
	private int rewardNum;
	
	
	
	public int getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}

	public String getRankRange() {
		return rankRange;
	}

	public void setRankRange(String rankRange) {
		this.rankRange = rankRange;
	}



	public String getId() {
		return id;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	

	public int getDailyOrRealtime() {
		return dailyOrRealtime;
	}

	public void setDailyOrRealtime(int dailyOrRealtime) {
		this.dailyOrRealtime = dailyOrRealtime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
