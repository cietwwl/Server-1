package com.rwbase.dao.fresherActivity.pojo;

public class FresherActivityFinalRewardCfg {
	private int id;
	private int progress;
	private String reward;
	private int activityCode;
	
	
	
	
	public int getActivityCode() {
		return activityCode;
	}
	public void setActivityCode(int activityCode) {
		this.activityCode = activityCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
}
