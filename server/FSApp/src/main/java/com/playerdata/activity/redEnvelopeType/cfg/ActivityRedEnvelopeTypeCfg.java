package com.playerdata.activity.redEnvelopeType.cfg;

import org.apache.commons.lang3.StringUtils;

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
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 1;	//是否服务端同步描述
	
	private String totalStartTimeStr;
	private String totalEndTimeStr;
	private String totalRewardsTimeStr;

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
	public void setViceStartTime(String startExTime) {
		setStartTime(startExTime);
	}

	@Override
	public void setViceEndTime(String endExTime) {
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
		String tmpStartStr = ActivityTimeHelper.getThisZoneTime(totalStartTimeStr);
		String tmpEndStr = ActivityTimeHelper.getThisZoneTime(totalEndTimeStr);
		String tmpRewardTimeStr = ActivityTimeHelper.getThisZoneTime(totalRewardsTimeStr);
		if(StringUtils.isNotBlank(tmpStartStr)){
			startTimeStr = tmpStartStr;
		}
		if(StringUtils.isNotBlank(tmpEndStr)){
			endTimeStr = tmpEndStr;
		}
		if(StringUtils.isNotBlank(tmpRewardTimeStr)){
			getRewardsTimeStr = tmpRewardTimeStr;
		}
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
	
	@Override
	public String getActDesc() {
		if(0 != isSynDesc){
			return titleBG;
		}
		return null;
	}
}
