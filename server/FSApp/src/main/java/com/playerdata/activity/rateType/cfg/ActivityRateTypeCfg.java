package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
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
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
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
		return adStartTimeStr;
	}

	@Override
	public String getViceEndTime() {
		return endTimeStr;
	}

	@Override
	public void setRangeTime(String rangeTime) {
		timeStr = rangeTime;
	}

	@Override
	public String getRangeTime() {
		return timeStr;
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
	public void setStartAndEndTime(String adStartTimeStr, String adEndTimeStr) {
		this.adStartTimeStr = adStartTimeStr;
		this.adEndTimeStr = adEndTimeStr;
		ExtraInitAfterLoad();
	}
	
	@Override
	public void setViceStartAndEndTime(String startTimeStr, String endTimeStr) {
		this.startTimeStr = startTimeStr;
		this.endTimeStr = endTimeStr;
		ExtraInitViceAfterLoad();
	}
	
	@Override
	public void ExtraInitAfterLoad(){
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(adStartTimeStr, adEndTimeStr);
		if(null == timePair) return;
		adStartTimeStr = timePair.getStartTime();
		adEndTimeStr = timePair.getEndTime();
		ExtraInitViceAfterLoad();
	}
	
	@Override
	public void setActDesc(String actDesc) {
		timeDesc = actDesc;
	}
}
