package com.playerdata.activityCommon.modifiedActivity;

import java.util.HashMap;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class ActivityModifyItem {
	
	private int id;
	
	private String startTime;
	
	private String endTime;
	
	private HashMap<Integer, String> rewardStrMap;
	
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
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
