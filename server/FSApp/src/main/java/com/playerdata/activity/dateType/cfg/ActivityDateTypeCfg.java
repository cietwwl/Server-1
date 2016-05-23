package com.playerdata.activity.dateType.cfg;


public class ActivityDateTypeCfg {

	private String id;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	
	private String desc;	

	private int sortNum;
	
	//活动总计数
	private int awardDount;
	//总计数奖励
	private String awardGift;
	
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	

	public String getId() {
		return id;
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

	public int getAwardDount() {
		return awardDount;
	}

	public String getAwardGift() {
		return awardGift;
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
