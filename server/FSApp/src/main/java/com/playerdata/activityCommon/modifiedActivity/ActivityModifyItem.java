package com.playerdata.activityCommon.modifiedActivity;

import java.util.HashMap;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class ActivityModifyItem {
	
	private int id;
	
	private String startTime;
	
	private String endTime;
	
	private String startViceTime;
	
	private String endViceTime;
	
	private String actDesc;
	
	private String rangeTime;
	
	private HashMap<Integer, String> rewardStrMap = new HashMap<Integer, String>();
	
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

	public String getStartViceTime() {
		return startViceTime;
	}

	public void setStartViceTime(String startViceTime) {
		this.startViceTime = startViceTime;
	}

	public String getEndViceTime() {
		return endViceTime;
	}

	public void setEndViceTime(String endViceTime) {
		this.endViceTime = endViceTime;
	}
	
	public String getActDesc() {
		return actDesc;
	}

	public void setActDesc(String actDesc) {
		this.actDesc = actDesc;
	}

	public String getRangeTime() {
		return rangeTime;
	}

	public void setRangeTime(String rangeTime) {
		this.rangeTime = rangeTime;
	}
}
