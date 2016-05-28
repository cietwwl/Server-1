package com.rw.handler.daily;

public class DailyActivityData{
	private int taskId; //任务id
	private int canGetReward; //是否可以领取奖励,1可以，0不可以
	private int currentProgress; //当前的进度;
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getCanGetReward() {
		return canGetReward;
	}
	public void setCanGetReward(int canGetReward) {
		this.canGetReward = canGetReward;
	}
	public int getCurrentProgress() {
		return currentProgress;
	}
	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}
	
	public boolean notFinish(){
		return canGetReward ==0;
	}
}
