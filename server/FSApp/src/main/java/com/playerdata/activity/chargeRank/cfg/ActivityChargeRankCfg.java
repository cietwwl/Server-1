package com.playerdata.activity.chargeRank.cfg;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class ActivityChargeRankCfg implements ActivityCfgIF{
	private int id; //活动id
	private String title;	//活动的标题
	private String startTimeStr; //开始时间
	private String endTimeStr; //结束时间
	private int isAutoRefresh; //是否隔天自动刷新
	private int levelLimit; //开启等级
	private int version; //活动版本
	private long startTime;	//活动的开启时间
	private long endTime;	//活动的结束时间
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 0;	//是否服务端同步描述

	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}
	
	public String getEndTimeStr() {
		return endTimeStr;
	}
	
	public int getIsAutoRefresh() {
		return isAutoRefresh;
	}
	
	public int getLevelLimit() {
		return levelLimit;
	}
	
	public int getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getCfgId() {
		return id;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public int getVipLimit() {
		return 0;
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
