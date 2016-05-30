package com.playerdata.fixEquip;

public class FixEquipResult {

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
	
	public static FixEquipResult newInstance(boolean success){
		
		FixEquipResult activityComResult = new FixEquipResult();
		activityComResult.setSuccess(success);
		
		return activityComResult;
	}
	
	
	
}
