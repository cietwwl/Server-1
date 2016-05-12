package com.playerdata.activity.timeCountType.cfg;



public class ActivityTimeCountTypeCfg {

	private String id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	
	private String desc;
	
	private String group;
	
	private String version;
	
	
	

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	private int sortNum;
	
	private String countLimit;


	public String getId() {
		return id;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getCion() {
		return cion;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleBG() {
		return titleBG;
	}

	public String getDesc() {
		return desc;
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

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}
