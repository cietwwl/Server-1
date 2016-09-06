package com.playerdata.activity.dailyCharge.cfg;
import com.common.BaseConfig;
import com.rw.fsutil.util.DateUtils;

public class ActivityDailyChargeCfg extends BaseConfig {
	private int id; //活动id
	private String title; //标题
	private String titleBG; //右上角说明文字
	private int levelLimit; //开启等级
	private String startTimeStr; //开始时间
	private long startTime;
	private String endTimeStr; //结束时间
	private long endTime;
	private int version; //活动版本

	public int getId() {
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
 		startTime = DateUtils.YyyymmddhhmmToMillionseconds(startTimeStr);
		endTime = DateUtils.YyyymmddhhmmToMillionseconds(endTimeStr);	
 	}
}
