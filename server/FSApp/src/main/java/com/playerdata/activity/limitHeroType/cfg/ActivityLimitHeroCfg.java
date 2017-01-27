package com.playerdata.activity.limitHeroType.cfg;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityLimitHeroCfg implements ActivityCfgIF{

	private int id;
	
	private String emailTitle;
	
	private long startTime;

	private long endTime;

	private String startTimeStr;

	private String endTimeStr;
	
	private int singleintegral;//单抽涨分
	
	private int tenintegral;//十连涨分

	private int version;

	private int levelLimit;

	private int rankNumer;
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 1;	//是否服务端同步描述
	
	private String totalStartTimeStr;
	private String totalEndTimeStr;

	public int getRankNumer() {
		return rankNumer;
	}

	public void setRankNumer(int rankNumer) {
		this.rankNumer = rankNumer;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	public int getSingleintegral() {
		return singleintegral;
	}

	public void setSingleintegral(int singleintegral) {
		this.singleintegral = singleintegral;
	}

	public int getTenintegral() {
		return tenintegral;
	}

	public void setTenintegral(int tenintegral) {
		this.tenintegral = tenintegral;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
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
	public void setVersion(int version) {
		this.version = version;
	}

 	public void ExtraInitAfterLoad() {
 		String tmpStartStr = ActivityTimeHelper.getThisZoneTime(totalStartTimeStr);
		String tmpEndStr = ActivityTimeHelper.getThisZoneTime(totalEndTimeStr);
		if(StringUtils.isNotBlank(tmpStartStr)){
			startTimeStr = tmpStartStr;
		}
		if(StringUtils.isNotBlank(tmpEndStr)){
			endTimeStr = tmpEndStr;
		}
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
	public String getActDesc() {
		if(0 != isSynDesc){
			return titleBG;
		}
		return null;
	}
}
