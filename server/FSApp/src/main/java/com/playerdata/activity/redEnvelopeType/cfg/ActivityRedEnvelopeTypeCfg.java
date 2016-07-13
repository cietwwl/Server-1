package com.playerdata.activity.redEnvelopeType.cfg;


public class ActivityRedEnvelopeTypeCfg {

	private String id;

	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	private int levelLimit;

	private String version;
	
	private String getRewardsTimeStr;
	
	private long getRewardsTime;
	
	private String emailTitle;


	public String getId() {
		return id;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	


	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
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

	public String getGetRewardsTimeStr() {
		return getRewardsTimeStr;
	}

	public void setGetRewardsTimeStr(String getRewardsTimeStr) {
		this.getRewardsTimeStr = getRewardsTimeStr;
	}

	public long getGetRewardsTime() {
		return getRewardsTime;
	}

	public void setGetRewardsTime(long getRewardsTime) {
		this.getRewardsTime = getRewardsTime;
	}

	
	
	
}
