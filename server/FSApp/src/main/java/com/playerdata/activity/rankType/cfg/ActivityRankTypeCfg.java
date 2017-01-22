package com.playerdata.activity.rankType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityRankTypeCfg implements ActivityCfgIF{

	private int id;

	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;

	private int levelLimit;

	private int version;
	
	private int dailyOrRealtime;
	
	private String rankRange;
	
	private int rewardNum;
	
	private int enumId;

	public int getId() {
		return enumId;
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

	public int getLevelLimit() {
		return levelLimit;
	}

	@Override
	public int getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	public int getDailyOrRealtime() {
		return dailyOrRealtime;
	}

	public String getRankRange() {
		return rankRange;
	}

	public int getRewardNum() {
		return rewardNum;
	}

	public int getEnumId() {
		return enumId;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Override
	public int getCfgId() {
		return id;
	}

	@Override
	public int getVipLimit() {
		return 0;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return false;
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
