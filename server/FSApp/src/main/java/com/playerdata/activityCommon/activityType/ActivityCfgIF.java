package com.playerdata.activityCommon.activityType;

public interface ActivityCfgIF {
	
	int getId();
	
	long getStartTime();
	
	long getEndTime();
	
	public int getVersion();
	
	public int getLevelLimit();
}
