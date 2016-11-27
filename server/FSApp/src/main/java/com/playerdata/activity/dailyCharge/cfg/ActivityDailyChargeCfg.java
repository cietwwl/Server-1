package com.playerdata.activity.dailyCharge.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.ActivityTimeHelper;
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

 	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	@Override
 	public void ExtraInitAfterLoad() {
 		startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		endTime = ActivityTimeHelper.cftEndTimeToLong(startTime, endTimeStr);
 	}

	@Override
	public boolean isDailyRefresh() {
		return isDailyRefresh == 1;
	}
	
	@Override
	public int getVipLimit() {
		return 0;
	}
}
