package com.playerdata.activity.shakeEnvelope.cfg;
import java.util.ArrayList;
import java.util.List;

import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;

public class ActivityShakeEnvelopeCfg implements ActivityCfgIF, ActivityExtendTimeIF, ActivityRangeTimeIF{
	
	private int id; //活动id
	private int enumId; //活动的类型id
	private String startTimeStr; //开始时间
	private String endTimeStr; //结束时间
	private String adStartTimeStr; //开始时间
	private String adEndTimeStr; //结束时间
	private String timeStr;	//活动开启时间段
	
	private String timeDesc;	//活动时间的描述
	private String titleBG;		//活动的描述
	
	private int duration;	//开启后持续时间
	private int interval;	//开启间隔时间
	private String dropStr;	//掉落方案
	private int levelLimit; //开启等级
	private int vipLimit;
	private int version; //活动版本
	
	private long startTime;	//活动的开启时间
	private long endTime;	//活动的结束时间
	private List<TimeSectionPair> sections = new ArrayList<TimeSectionPair>();

	public int getEnumId() {
		return enumId;
	}

	public String getAdStartTimeStr() {
		return adStartTimeStr;
	}

	public String getAdEndTimeStr() {
		return adEndTimeStr;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public String getTimeDesc() {
		return timeDesc;
	}

	public String getTitleBG() {
		return titleBG;
	}

	public int getDuration() {
		return duration;
	}

	public int getInterval() {
		return interval;
	}

	public String getDropStr() {
		return dropStr;
	}

	public List<TimeSectionPair> getSections() {
		return sections;
	}

	@Override
	public boolean isDailyRefresh() {
		return true;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return true;
	}
	
 	public void ExtraInitAfterLoad() {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, endTimeStr);
		if(null == timePair) return;
		startTime = timePair.getStartMil();
		endTime = timePair.getEndMil();
		startTimeStr = timePair.getStartTime();
		endTimeStr = timePair.getEndTime();
		reAnalyzeRangeTime();
 	}
	
	@Override
	public void setStartAndEndTime(String startTimeStr, String endTimeStr) {
		this.startTimeStr = startTimeStr;
		this.endTimeStr = endTimeStr;
		ExtraInitAfterLoad();
	}

	@Override
	public String getActDesc() {
		return timeDesc;
	}

	@Override
	public void setActDesc(String actDesc) {
		timeDesc = actDesc;
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
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public int getLevelLimit() {
		return levelLimit;
	}

	@Override
	public int getVipLimit() {
		return vipLimit;
	}

	@Override
	public String getStartTimeStr() {
		return startTimeStr;
	}

	@Override
	public String getEndTimeStr() {
		return endTimeStr;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void setRangeTime(String rangeTime) {
		timeStr = rangeTime;
		reAnalyzeRangeTime();
	}
	
	public void reAnalyzeRangeTime(){
		List<TimeSectionPair> newSections = new ArrayList<TimeSectionPair>();
		String[] sectionStrs = timeStr.split(",");
		int interTime = interval * 60 * 1000;
		int duraTime = duration * 60 * 1000;
		for(String sectionStr : sectionStrs){
			String[] startAndEndStr = sectionStr.split(":");
			if(startAndEndStr.length == 2){
				String startSt = startAndEndStr[0];
				long startTime = Integer.valueOf(startSt.substring(0, 2)) * 60 * 60 * 1000;
				startTime += Integer.valueOf(startSt.substring(2, 4)) * 60 * 1000;
				startTime += Integer.valueOf(startSt.substring(4, 6)) * 1000;
				String endSt = startAndEndStr[1];
				long endTime = Integer.valueOf(endSt.substring(0, 2)) * 60 * 60 * 1000;
				endTime += Integer.valueOf(endSt.substring(2, 4)) * 60 * 1000;
				endTime += Integer.valueOf(endSt.substring(4, 6)) * 1000;
				while(startTime < endTime){
					TimeSectionPair section = new TimeSectionPair();
					section.setStartTime(startTime);
					section.setEndTime(startTime + duraTime);
					newSections.add(section);
					startTime += interTime;
				}
			}
		}
		sections = newSections;
	}

	@Override
	public String getRangeTime() {
		return timeStr;
	}

	@Override
	public String getViceStartTime() {
		return adStartTimeStr;
	}

	@Override
	public String getViceEndTime() {
		return adEndTimeStr;
	}
	
	@Override
	public void setViceStartAndEndTime(String startViceTime, String endViceTime) {
		adStartTimeStr = startViceTime;
		adEndTimeStr = endViceTime;
	}
}
