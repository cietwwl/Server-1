package com.rwbase.dao.openLevelTiggerService.pojo;



public class OpenLevelTiggerServiceSubItem {
	private int triggerNumber;//Id
	private int triggerTime;
	private boolean isOver;
	private boolean isGivePower;//是否自动赠送体力
	private String userId;//机器人对象
	
	public int getTriggerNumber() {
		return triggerNumber;
	}
	public void setTriggerNumber(int triggerNumber) {
		this.triggerNumber = triggerNumber;
	}
	public int getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(int triggerTime) {
		this.triggerTime = triggerTime;
	}
	public boolean isOver() {
		return isOver;
	}
	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}
	public boolean isGivePower() {
		return isGivePower;
	}
	public void setGivePower(boolean isGivePower) {
		this.isGivePower = isGivePower;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
