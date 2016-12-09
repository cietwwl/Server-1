package com.playerdata.activityCommon.modifiedActivity;

import java.util.HashMap;

public class ActivityModifyItem {
	
	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private HashMap<Integer, String> rewardStrMap;
	
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public HashMap<Integer, String> getRewardStrMap() {
		return rewardStrMap;
	}

	public void setRewardStrMap(HashMap<Integer, String> rewardStrMap) {
		this.rewardStrMap = rewardStrMap;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
