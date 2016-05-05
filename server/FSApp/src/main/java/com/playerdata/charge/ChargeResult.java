package com.playerdata.charge;

public class ChargeResult {

	private ChargeResult(){
		
	}
	
	public static ChargeResult newResult(boolean success){
		ChargeResult result = new ChargeResult();
		result.setSuccess(success);
		return result;
	}
	
	private boolean success;
	
	private String tips;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}
	
	
}
