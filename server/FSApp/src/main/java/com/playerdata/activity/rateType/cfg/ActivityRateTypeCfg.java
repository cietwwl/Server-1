package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;


public class ActivityRateTypeCfg implements ActivityCfgIF, ActivityExtendTimeIF, ActivityRangeTimeIF{

	private int id;	
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String adStartTimeStr;
	
	private String adEndTimeStr;
	
	private String timeStr;
	
	private List<ActivityRateTypeStartAndEndHourHelper> startAndEnd = new ArrayList<ActivityRateTypeStartAndEndHourHelper>();
	
	private String cion;
	
	private int levelLimit;
	
	private int vipLimit;
	
	/**
	 * 多倍的副本类型和产出类型组合，类型_产出类型#产出类型,类型_产出类型
	 */
	private String copytypeAndespecialitemidEnum;
	
	private Map<Integer, List<Integer>> copyTypeMap = new HashMap<Integer, List<Integer>>();
	
	private int enumId;
	
	private String title;
	
	private String titleBG;
	
	private String timeDesc;
	
	private int sortNum;
	
	private float rate;

	private int version;
	
	private int multiple;
	
	private int isAutoRefresh;
	
	private String totalStartTimeStr;
	private String totalEndTimeStr;
	private String totalAdStartTimeStr;
	private String totalAdEndTimeStr;
	
	public int getEnumId() {
		return enumId;
	}

	public void setEnumId(int enumId) {
		this.enumId = enumId;
	}

	public Map<Integer, List<Integer>> getCopyTypeMap() {
		return copyTypeMap;
	}

	public void setCopyTypeMap(Map<Integer, List<Integer>> copyTypeMap) {
		this.copyTypeMap = copyTypeMap;
	}

	public String getCopytypeAndespecialitemidEnum() {
		return copytypeAndespecialitemidEnum;
	}

	public void setCopytypeAndespecialitemidEnum(
			String copytypeAndespecialitemidEnum) {
		this.copytypeAndespecialitemidEnum = copytypeAndespecialitemidEnum;
	}

	public List<ActivityRateTypeStartAndEndHourHelper> getStartAndEnd() {
		return startAndEnd;
	}

	public void setStartAndEnd(
			List<ActivityRateTypeStartAndEndHourHelper> startAndEnd) {
		this.startAndEnd = startAndEnd;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
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

	public int getSortNum() {
		return sortNum;
	}

	public float getRate() {
		return rate;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getStartTimeStr() {
		return adStartTimeStr;
	}

	public String getEndTimeStr() {
		return adEndTimeStr;
	}

	public String getTimeStr() {
		return timeStr;
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
		return isAutoRefresh == 1;
	}

	@Override
	public boolean isEveryDaySame() {
		return false;
	}

 	public void ExtraInitAfterLoad() {
 		String tmpStartStr = ActivityTimeHelper.getThisZoneTime(totalStartTimeStr);
		String tmpEndStr = ActivityTimeHelper.getThisZoneTime(totalEndTimeStr);
		String tmpAdStartStr = ActivityTimeHelper.getThisZoneTime(totalAdStartTimeStr);
		String tmpAdEndStr = ActivityTimeHelper.getThisZoneTime(totalAdEndTimeStr);
		if(StringUtils.isNotBlank(tmpStartStr)){
			startTimeStr = tmpStartStr;
		}
		if(StringUtils.isNotBlank(tmpEndStr)){
			endTimeStr = tmpEndStr;
		}
		if(StringUtils.isNotBlank(tmpAdStartStr)){
			adStartTimeStr = tmpStartStr;
		}
		if(StringUtils.isNotBlank(tmpAdEndStr)){
			adEndTimeStr = tmpEndStr;
		}
 		startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		endTime = ActivityTimeHelper.cftEndTimeToLong(startTime, endTimeStr);
 	}

	@Override
	public void setStartTime(String startTimeStr) {
		this.adStartTimeStr = startTimeStr;
	}

	@Override
	public void setEndTime(String endTimeStr) {
		this.adEndTimeStr = endTimeStr;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String getActDesc() {
		return timeDesc;
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
		this.startTime = ActivityTimeHelper.cftStartTimeToLong(startExTime);
		this.startTimeStr = startExTime;
	}

	@Override
	public void setViceEndTime(String endExTime) {
		this.endTime = ActivityTimeHelper.cftEndTimeToLong(this.startTime, endExTime);
		this.endTimeStr = endExTime;
	}

	@Override
	public void setRangeTime(String rangeTime) {
		timeStr = rangeTime;
	}

	@Override
	public String getRangeTime() {
		return timeStr;
	}
}
