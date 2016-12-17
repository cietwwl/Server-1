package com.playerdata.activity.dailyCharge.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class ActivityDailyChargeCfg extends BaseConfig implements ActivityCfgIF{
	private int id; //活动id
	private String title; //标题
	private String titleBG; //右上角说明文字
	private int levelLimit; //开启等级
	private String startTimeStr; //开始时间
	private long startTime;
	private String endTimeStr; //结束时间
	private long endTime;
	private int version; //活动版本
	private int isDailyRefresh;
	
	private int isSynDesc = 0;	//是否服务端同步描述

	public int getId() {
		return id;
 	}
	
	public int getCfgId() {
		return id;
 	}
	
	public String getTitle() {
		return title;
	}
	
	public String getTitleBG() {
		return titleBG;
	}
	
 	public int getLevelLimit() {
 		return levelLimit;
 	}
 	
 	public String getStartTimeStr() {
 		return startTimeStr;
 	}
 	
 	public String getEndTimeStr() {
 		return endTimeStr;
 	}
 	
 	public int getVersion() {
 		return version;
 	}
 	
 	@Override
	public void setVersion(int version) {
		this.version = version;
	}

 	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, endTimeStr);
		if(null == timePair) return;
		startTime = timePair.getStartMil();
		endTime = timePair.getEndMil();
		startTimeStr = timePair.getStartTime();
		endTimeStr = timePair.getEndTime();
 	}
	
	@Override
	public void setStartAndEndTime(String startTimeStr, String endTimeStr) {
		this.startTimeStr = startTimeStr;
		this.endTimeStr = endTimeStr;
		ExtraInitAfterLoad();
	}

	@Override
	public boolean isDailyRefresh() {
		return isDailyRefresh == 1;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return false;
	}
	
	@Override
	public int getVipLimit() {
		return 0;
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
