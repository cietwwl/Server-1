package com.playerdata.activity;

public class ActivityComResult {

	private boolean success;
	
	private String reason;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public static ActivityComResult newInstance(boolean success){
		ActivityComResult activityComResult = new ActivityComResult();
		activityComResult.setSuccess(success);
		activityComResult.setReason("没有可以领取的奖励");
		return activityComResult;
	}
	
	
	
}
