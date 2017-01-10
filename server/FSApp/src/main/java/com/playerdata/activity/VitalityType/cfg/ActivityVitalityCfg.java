package com.playerdata.activity.VitalityType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
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
	
	private int isDailyRefresh = 0;	//是否每天刷新
	private int isSame = 1;	//是否每天用相同的子项

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
		return isDailyRefresh == 1;
	}

	@Override
	public boolean isEveryDaySame() {
		return isSame == 1;
	}

	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, endTimeStr);
		if(null == timePair) return;
		startTime = timePair.getStartMil();
		endTime = timePair.getEndMil();
		startTimeStr = timePair.getStartTime();
		endTimeStr = timePair.getEndTime();
 	}
	
	@Override
	public void setStartAndEndTime(String startTimeStr, String endTimeStr) {
		this.startTimeStr = startTimeStr;
		this.endTimeStr = endTimeStr;
		ExtraInitAfterLoad();
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
	
	@Override
	public void setActDesc(String actDesc) {
		titleBG = actDesc;
	}
}
