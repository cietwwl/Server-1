package com.playerdata.activityCommon.activityType;

public interface ActivityCfgIF extends ActivityTimeInitIF{
	
	public int getId();
	
	public int getCfgId();
	
	public long getStartTime();
	
	public long getEndTime();
	
	public int getLevelLimit();
	
	public int getVipLimit();
	
	public boolean isDailyRefresh();
	
	public boolean isEveryDaySame();
	
	public void setStartAndEndTime(String startTime, String endTime);
	
	public String getStartTimeStr();
	
	public String getEndTimeStr();
	
	public int getVersion();
	
	public void setVersion(int version);
	
	public String getActDesc();
	
	public void setActDesc(String actDesc);
}
