package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;


public class ActivityRateTypeCfg implements ActivityCfgIF{

	private int id;	
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
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
	
	private String desc;
	
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

	public String getDesc() {
		return desc;
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

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	@Override
	public int getId() {
		return enumId;
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
	public void setVersion(int version) {
		this.version = version;
	}
}
