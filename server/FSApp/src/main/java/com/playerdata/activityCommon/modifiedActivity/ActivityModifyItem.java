package com.playerdata.activityCommon.modifiedActivity;

public class ActivityModifyItem {
	
	private int id;
	
	private long startTime;
	
	private long endTime;
	
	private String rewardStr;
	
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

	public String getRewardStr() {
		return rewardStr;
	}

	public void setRewardStr(String rewardStr) {
		this.rewardStr = rewardStr;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
