package com.playerdata.activityCommon.activityType;

public interface ActivityCfgIF {
	
	public int getId();
	
	public int getCfgId();
	
	public long getStartTime();
	
	public long getEndTime();
	
	public int getVersion();
	
	public int getLevelLimit();
	
	public int getVipLimit();
	
	public boolean isDailyRefresh();
	
	public boolean isEveryDaySame();
	
	public void setStartTime(String startTime);
	
	public void setEndTime(String endTime);
	
	public String getStartTimeStr();
	
	public String getEndTimeStr();
	
	public void setVersion(int version);
	
	public String getActDesc();
}
