package com.playerdata.activity.exChangeType.cfg;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
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
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 0;	//是否服务端同步描述
	
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
	public String getViceStartTime() {
		return dropStartTimeStr;
	}

	@Override
	public String getViceEndTime() {
		return dropEndTimeStr;
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
	public long getStartTime() {
		return changeStartTime;
	}

	@Override
	public long getEndTime() {
		return changeEndTime;
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
 	
 	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(changeStartTimeStr, changeEndTimeStr);
		changeStartTime = timePair.getStartMil();
		changeEndTime = timePair.getEndMil();
		changeStartTimeStr = timePair.getStartTime();
		changeEndTimeStr = timePair.getEndTime();
 	}
	
	public void ExtraInitViceAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(dropStartTimeStr, dropEndTimeStr);
		dropStartTime = timePair.getStartMil();
		dropEndTime = timePair.getEndMil();
		dropStartTimeStr = timePair.getStartTime();
		dropEndTimeStr = timePair.getEndTime();
 	}
	
	@Override
	public void setStartAndEndTime(String changeStartTimeStr, String changeEndTimeStr) {
		this.changeStartTimeStr = changeStartTimeStr;
		this.changeEndTimeStr = changeEndTimeStr;
		ExtraInitAfterLoad();
	}
	
	@Override
	public void setViceStartAndEndTime(String dropStartTimeStr, String dropEndTimeStr) {
		this.dropStartTimeStr = dropStartTimeStr;
		this.dropEndTimeStr = dropEndTimeStr;
		ExtraInitViceAfterLoad();
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
	
	@Override
	public String getActDesc() {
		if(0 != isSynDesc){
			return titleBG;
		}
		return null;
	}
}
