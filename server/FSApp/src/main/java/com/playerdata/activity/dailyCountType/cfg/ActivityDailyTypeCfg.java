package com.playerdata.activity.dailyCountType.cfg;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityDailyTypeCfg implements ActivityCfgIF{

	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private int version;
	
	private int levelLimit;
	
	private int enumId;
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 0;	//是否服务端同步描述
	
	private String totalStartTimeStr;
	private String totalEndTimeStr;
	
	public int getEnumId() {
		return enumId;
	}

	public void setEnumId(int enumId) {
		this.enumId = enumId;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	private int sortNum;
	
	private String countLimit;


	public int getId() {
		return enumId;
	}

	public long getEndTime() {
		return endTime;
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
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
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
	public int getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
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
		return true;
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
