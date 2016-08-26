package com.playerdata.activity.limitHeroType.cfg;




public class ActivityLimitHeroCfg {

	private String id;
	
	private String emailTitle;
	
	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;
	
	private int singleintegral;//单抽涨分
	
	private int tenintegral;//十连涨分
	

	private String version;

	private int levelLimit;

	private int rankNumer;


	public int getRankNumer() {
		return rankNumer;
	}

	public void setRankNumer(int rankNumer) {
		this.rankNumer = rankNumer;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	public int getSingleintegral() {
		return singleintegral;
	}

	public void setSingleintegral(int singleintegral) {
		this.singleintegral = singleintegral;
	}

	public int getTenintegral() {
		return tenintegral;
	}

	public void setTenintegral(int tenintegral) {
		this.tenintegral = tenintegral;
	}



	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
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
