package com.bm.groupChamp.service;

public class GroupChampResult {

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
	
	public static GroupChampResult newInstance(boolean success){
		
		GroupChampResult activityComResult = new GroupChampResult();
		activityComResult.setSuccess(success);
		
		return activityComResult;
	}
	
	
	
}
