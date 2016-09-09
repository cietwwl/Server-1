package com.bm.worldBoss.service;


public class WBResult {

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
	
	public static WBResult newInstance(boolean success){
		
		WBResult result = new WBResult();
		result.setSuccess(success);
		
		return result;
	}
	
	
	
}
