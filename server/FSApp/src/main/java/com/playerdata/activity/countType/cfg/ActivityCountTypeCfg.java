package com.playerdata.activity.countType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityCountTypeCfg implements ActivityCfgIF{

	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	
	private String desc;	

	private String goToType;
	
	private String group;
		
	//每天刷新
	private int isAutoRefresh ;
	
	private int version;

	private int levelLimit;
	
	private String enumId;
	
	private int sortNum;
	
	private String countLimit;
	
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getId() {
		return Integer.valueOf(enumId);
	}
	
	public int getCfgId() {
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

	public String getGoToType() {
		return goToType;
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

	public int getIsAutoRefresh() {
		return isAutoRefresh;
	}

	public void setIsAutoRefresh(int isAutoRefresh) {
		this.isAutoRefresh = isAutoRefresh;
	}

	@Override
	public boolean isDailyRefresh() {
		return 0 != isAutoRefresh;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return false;
	}
	
	@Override
	public int getVipLimit() {
		return 0;
	}

	@Override
	public void setStartTime(String startTimeStr) {
		this.startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		this.startTimeStr = startTimeStr;
	}

	@Override
	public void setEndTime(String endTimeStr) {
		this.endTime = ActivityTimeHelper.cftEndTimeToLong(this.startTime, endTimeStr);
		this.endTimeStr = endTimeStr;
	}
}
