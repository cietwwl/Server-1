package com.bm.saloon;


public class SaloonResult {


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
	
	public static SaloonResult newInstance(boolean success){
		
		SaloonResult comResult = new SaloonResult();
		comResult.setSuccess(success);
		
		return comResult;
	}
	
	
	

}
