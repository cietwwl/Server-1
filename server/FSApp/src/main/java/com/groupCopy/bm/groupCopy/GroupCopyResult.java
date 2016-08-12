package com.groupCopy.bm.groupCopy;

public class GroupCopyResult {

	private boolean success;
	
	private String tipMsg;
	
	private Object item;
	
	private GroupCopyResult(){
		
	}
	
	public static GroupCopyResult newResult(){
		return new GroupCopyResult();
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public GroupCopyResult setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public String getTipMsg() {
		return tipMsg;
	}

	public GroupCopyResult setTipMsg(String tipMsg) {
		this.tipMsg = tipMsg;
		return this;
	}

	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}
	
	
	
	
}
