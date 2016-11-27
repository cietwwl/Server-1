package com.playerdata.activity.evilBaoArrive.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class EvilBaoArriveCfg extends BaseConfig implements ActivityCfgIF{
	private int id; //活动id
	private String title;	//活动的标题
	private String startTimeStr; //开始时间
	private String endTimeStr; //结束时间
	private int isAutoRefresh; //是否隔天自动刷新
	private int levelLimit; //开启等级
	private int version; //活动版本
	private long startTime;	//活动的开启时间
	private long endTime;	//活动的结束时间

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
 	public void ExtraInitAfterLoad() {
 		startTime = ActivityTimeHelper.cftStartTimeToLong(startTimeStr);
		endTime = ActivityTimeHelper.cftEndTimeToLong(startTime, endTimeStr);
 	}
}
