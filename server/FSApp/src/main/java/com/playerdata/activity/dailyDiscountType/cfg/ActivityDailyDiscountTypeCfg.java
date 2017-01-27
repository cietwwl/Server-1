package com.playerdata.activity.dailyDiscountType.cfg;

import org.apache.commons.lang3.StringUtils;

import com.common.BaseConfig;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;



public class ActivityDailyDiscountTypeCfg extends BaseConfig implements ActivityCfgIF{

	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private int version;
	
	private int levelLimit;
	
	private int refreshTime;
	
	private int enumId;
	
	private int isRefresh;
	
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
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
		return isRefresh == 1;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return false;
	}

	@Override
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
