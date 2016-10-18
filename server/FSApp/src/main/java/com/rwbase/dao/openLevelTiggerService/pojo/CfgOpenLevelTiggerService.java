package com.rwbase.dao.openLevelTiggerService.pojo;

public class CfgOpenLevelTiggerService {
	private int type; //功能ID  triggerTime  triggerNumber
	private int triggerTime;
	private int triggerNumber;
	private boolean isGive;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(int triggerTime) {
		this.triggerTime = triggerTime;
	}
	public int getTriggerNumber() {
		return triggerNumber;
	}
	public void setTriggerNumber(int triggerNumber) {
		this.triggerNumber = triggerNumber;
	}
	public boolean isGive() {
		return isGive;
	}
	public void setGive(boolean isGive) {
		this.isGive = isGive;
	}
	
	
	
}
