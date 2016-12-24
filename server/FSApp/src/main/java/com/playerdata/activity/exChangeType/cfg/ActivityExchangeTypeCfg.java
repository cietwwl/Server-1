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
	
	private int isDailyRefresh = 1;
	
	public String getEnumId() {
		return String.valueOf(enumId);
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public long getDropStartTime() {
		return dropStartTime;
	}

	public long getDropEndTime() {
		return dropEndTime;
	}

	public String getDropStartTimeStr() {
		return dropStartTimeStr;
	}

	public String getDropEndTimeStr() {
		return dropEndTimeStr;
	}

	public String getChangeStartTimeStr() {
		return changeStartTimeStr;
	}

	public String getChangeEndTimeStr() {
		return changeEndTimeStr;
	}

	public long getChangeStartTime() {
		return changeStartTime;
	}

	public long getChangeEndTime() {
		return changeEndTime;
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
		return enumId;
	}

	@Override
	public int getCfgId() {
		return id;
	}

	@Override
	public long getStartTime() {
		return changeStartTime < dropStartTime ? changeStartTime : dropStartTime;
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
		return 1 == isDailyRefresh;
	}

	@Override
	public boolean isEveryDaySame() {
		return true;
	}
 	
 	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(changeStartTimeStr, changeEndTimeStr);
		if(null == timePair) return;
		changeStartTime = timePair.getStartMil();
		changeEndTime = timePair.getEndMil();
		changeStartTimeStr = timePair.getStartTime();
		changeEndTimeStr = timePair.getEndTime();
		ExtraInitViceAfterLoad();
 	}
	
	public void ExtraInitViceAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(dropStartTimeStr, dropEndTimeStr);
		if(null == timePair) return;
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
		return changeStartTime < dropStartTime ? changeStartTimeStr : dropStartTimeStr;
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
	
	@Override
	public void setActDesc(String actDesc) {
		titleBG = actDesc;
	}
}
