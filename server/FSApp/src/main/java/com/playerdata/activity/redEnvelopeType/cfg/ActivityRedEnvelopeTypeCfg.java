package com.playerdata.activity.redEnvelopeType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;


public class ActivityRedEnvelopeTypeCfg implements ActivityCfgIF, ActivityExtendTimeIF{

	private int id;

	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	private int levelLimit;

	private int version;
	
	private String getRewardsTimeStr;
	
	private long getRewardsTime;
	
	private String emailTitle;
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 1;	//是否服务端同步描述

	public int getLevelLimit() {
		return levelLimit;
	}
	
	public String getEmailTitle() {
		return emailTitle;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return getRewardsTimeStr;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getGetRewardsTimeStr() {
		return getRewardsTimeStr;
	}

	public long getGetRewardsTime() {
		return getRewardsTime;
	}

	@Override
	public String getViceStartTime() {
		return startTimeStr;
	}

	@Override
	public String getViceEndTime() {
		return endTimeStr;
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
		return 0;
	}

	@Override
	public boolean isDailyRefresh() {
		return true;
	}

	@Override
	public boolean isEveryDaySame() {
		return false;
	}

	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, getRewardsTimeStr);
		if(null == timePair) return;
		getRewardsTime = timePair.getEndMil();
		getRewardsTimeStr = timePair.getEndTime();
		ExtraInitViceAfterLoad();
 	}
	
	public void ExtraInitViceAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, endTimeStr);
		if(null == timePair) return;
		startTime = timePair.getStartMil();
		endTime = timePair.getEndMil();
		startTimeStr = timePair.getStartTime();
		endTimeStr = timePair.getEndTime();
 	}
	
	@Override
	public void setStartAndEndTime(String startTimeStr, String getRewardsTimeStr) {
		this.getRewardsTimeStr = getRewardsTimeStr;
		ExtraInitAfterLoad();
	}
	
	@Override
	public void setViceStartAndEndTime(String startTimeStr, String endTimeStr) {
		this.startTimeStr = startTimeStr;
		this.endTimeStr = endTimeStr;
		ExtraInitViceAfterLoad();
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
