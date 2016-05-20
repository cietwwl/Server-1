package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.List;


public class ActivityRateTypeCfg {

	private String id;	
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String timeStr;
	
	private List<ActivityRateTypeStartAndEndHourHelper> startAndEnd = new ArrayList<ActivityRateTypeStartAndEndHourHelper>();
	
	private String cion;
	
	private int levelLimit;
	
	public List<ActivityRateTypeStartAndEndHourHelper> getStartAndEnd() {
		return startAndEnd;
	}

	public void setStartAndEnd(
			List<ActivityRateTypeStartAndEndHourHelper> startAndEnd) {
		this.startAndEnd = startAndEnd;
	}

	private String title;
	
	private String titleBG;
	
	private String desc;
	
	private int sortNum;
	
	private float rate;

	private String version;
	
	private int multiple;
	
	

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
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

	public float getRate() {
		return rate;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
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



	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}



	
	
	
	
	
}
