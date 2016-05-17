package com.bm.groupSecret;

public class GroupSecretComResult {

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
	
	public static GroupSecretComResult newInstance(boolean success){
		
		GroupSecretComResult activityComResult = new GroupSecretComResult();
		activityComResult.setSuccess(success);
		
		return activityComResult;
	}
	
	
	
}
