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
	
	public void setStartTime(long startTime);
	
	public void setEndTime(long endTime);
}
