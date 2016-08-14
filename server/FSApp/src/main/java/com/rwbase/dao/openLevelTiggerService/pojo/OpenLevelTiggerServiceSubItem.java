package com.rwbase.dao.openLevelTiggerService.pojo;

import com.rw.fsutil.dao.annotation.CombineSave;


public class OpenLevelTiggerServiceSubItem {
	private int triggerNumber;//Id
	private int triggerTime;
	private boolean isOver;
	
	
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
	
	
}
