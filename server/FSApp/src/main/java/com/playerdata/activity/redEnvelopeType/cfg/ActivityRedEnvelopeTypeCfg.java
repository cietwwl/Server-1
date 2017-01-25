package com.playerdata.activity.redEnvelopeType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
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

	@Override
	public String getExStartTime() {
		return startTimeStr;
	}

	@Override
	public String getExEndTime() {
		return getRewardsTimeStr;
	}

	@Override
	public void setExStartTime(String startExTime) {
		setStartTime(startExTime);
	}

	@Override
	public void setExEndTime(String endExTime) {
		this.endTime = ActivityTimeHelper.cftEndTimeToLong(this.startTime, endExTime);
		this.endTimeStr = endExTime;
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
 		startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		endTime = ActivityTimeHelper.cftEndTimeToLong(startTime, endTimeStr);
		getRewardsTime = ActivityTimeHelper.cftEndTimeToLong(startTime, getRewardsTimeStr);
 	}

	@Override
	public void setStartTime(String startTimeStr) {
		this.startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		this.startTimeStr = startTimeStr;
	}

	@Override
	public void setEndTime(String endTimeStr) {
		getRewardsTime = ActivityTimeHelper.cftEndTimeToLong(this.startTime, endTimeStr);
		getRewardsTimeStr = endTimeStr;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
}
