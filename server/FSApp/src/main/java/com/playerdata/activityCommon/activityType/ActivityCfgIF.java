package com.playerdata.activityCommon.activityType;

public interface ActivityCfgIF {
	
	public int getId();
	
	public long getStartTime();
	
	public long getEndTime();
	
	public int getVersion();
	
	public int getLevelLimit();
	
	public boolean isDailyRefresh();
}