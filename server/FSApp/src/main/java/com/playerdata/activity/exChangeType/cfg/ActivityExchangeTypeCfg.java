package com.playerdata.activity.exChangeType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;


public class ActivityExchangeTypeCfg implements ActivityCfgIF, ActivityExtendTimeIF{

	private int id;
	
	private int enumId;
	
	private long dropStartTime;

	private long dropEndTime;

	private String dropStartTimeStr;

	private String dropEndTimeStr;
	
	private String changeStartTimeStr;

	private String changeEndTimeStr;

	private long changeStartTime;

	private long changeEndTime;
	
	private int version;

	private int levelLimit;	
	
	public String getEnumId() {
		return String.valueOf(enumId);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public long getDropStartTime() {
		return dropStartTime;
	}

	public void setDropStartTime(long dropStartTime) {
		this.dropStartTime = dropStartTime;
	}

	public long getDropEndTime() {
		return dropEndTime;
	}

	public void setDropEndTime(long dropEndTime) {
		this.dropEndTime = dropEndTime;
	}

	public String getDropStartTimeStr() {
		return dropStartTimeStr;
	}

	public void setDropStartTimeStr(String dropStartTimeStr) {
		this.dropStartTimeStr = dropStartTimeStr;
	}

	public String getDropEndTimeStr() {
		return dropEndTimeStr;
	}

	public void setDropEndTimeStr(String dropEndTimeStr) {
		this.dropEndTimeStr = dropEndTimeStr;
	}

	public String getChangeStartTimeStr() {
		return changeStartTimeStr;
	}

	public void setChangeStartTimeStr(String changeStartTimeStr) {
		this.changeStartTimeStr = changeStartTimeStr;
	}

	public String getChangeEndTimeStr() {
		return changeEndTimeStr;
	}

	public void setChangeEndTimeStr(String changeEndTimeStr) {
		this.changeEndTimeStr = changeEndTimeStr;
	}

	public long getChangeStartTime() {
		return changeStartTime;
	}

	public void setChangeStartTime(long changeStartTime) {
		this.changeStartTime = changeStartTime;
	}

	public long getChangeEndTime() {
		return changeEndTime;
	}

	public void setChangeEndTime(long changeEndTime) {
		this.changeEndTime = changeEndTime;
	}

	@Override
	public String getExStartTime() {
		return changeStartTimeStr;
	}

	@Override
	public String getExEndTime() {
		return changeEndTimeStr;
	}

	@Override
	public void setExStartTime(String startExTime) {
		dropStartTime = ActivityTimeHelper.cftStartTimeToLong(startExTime);
		dropStartTimeStr = startExTime;
	}

	@Override
	public void setExEndTime(String endExTime) {
		dropEndTime = ActivityTimeHelper.cftEndTimeToLong(dropStartTime, endExTime);
		dropEndTimeStr = endExTime;
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
	public long getStartTime() {
		return dropStartTime;
	}

	@Override
	public long getEndTime() {
		return dropEndTime;
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
	public void setStartTime(String startTime) {
		changeStartTime = ActivityTimeHelper.cftStartTimeToLong(startTime);
		changeStartTimeStr = startTime;
	}

	@Override
	public void setEndTime(String endTime) {
		changeEndTime = ActivityTimeHelper.cftEndTimeToLong(changeStartTime, endTime);
		changeEndTimeStr = endTime;
	}
	
 	public void ExtraInitAfterLoad() {
		dropStartTime = ActivityTimeHelper.cftStartTimeToLong(dropStartTimeStr);
		dropEndTime = ActivityTimeHelper.cftEndTimeToLong(dropStartTime, dropEndTimeStr);
		changeStartTime = ActivityTimeHelper.cftStartTimeToLong(changeStartTimeStr);
		changeEndTime = ActivityTimeHelper.cftEndTimeToLong(changeStartTime, changeEndTimeStr);
 	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String getStartTimeStr() {
		return changeStartTimeStr;
	}

	@Override
	public String getEndTimeStr() {
		return changeEndTimeStr;
	}
}
