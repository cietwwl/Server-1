package com.playerdata.activity.VitalityType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityVitalityCfg implements ActivityCfgIF{

	private int id;
	
	private String title;
	
	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	// 活跃度能否领奖，0=可以，1=不可以
	private boolean isCanGetReward;
	
	private int enumID;

	private int version;

	private int levelLimit;
	
	private int vipLimit;
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 0;	//是否服务端同步描述

	public int getEnumID() {
		return enumID;
	}

	public boolean isCanGetReward() {
		return isCanGetReward;
	}

	public void setCanGetReward(boolean isCanGetReward) {
		this.isCanGetReward = isCanGetReward;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
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

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getCfgId() {
		return id;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public int getVipLimit() {
		return vipLimit;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}

	@Override
	public boolean isEveryDaySame() {
		return true;
	}

 	public void ExtraInitAfterLoad() {
 		startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		endTime = ActivityTimeHelper.cftEndTimeToLong(startTime, endTimeStr);
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

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String getActDesc() {
		if(0 != isSynDesc){
			return titleBG;
		}
		return null;
	}
}
