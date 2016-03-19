package com.rwbase.dao.task.pojo;

import com.rwbase.common.enu.eTaskFinishDef;

public class Task implements TaskIF{
	private int id;
	private eTaskFinishDef finishType;
	private int superType;
	private int drawState; //是否可以领取奖励,1可以，0不可以，2已领取
	private int curProgress; //当前的进度;
	private int totalProgress;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDrawState() {
		return drawState;
	}
	public void setDrawState(int canDraw) {
		this.drawState = canDraw;
	}
	public int getCurProgress() {
		return curProgress;
	}
	public void setCurProgress(int curProgress) {
		this.curProgress = curProgress;
	}
	public int getTotalProgress() {
		return totalProgress;
	}
	public void setTotalProgress(int totalProgress) {
		this.totalProgress = totalProgress;
	}
	public eTaskFinishDef getFinishType() {
		return finishType;
	}
	public void setFinishType(eTaskFinishDef finishType) {
		this.finishType = finishType;
	}
	public int getSuperType() {
		return superType;
	}
	public void setSuperType(int superType) {
		this.superType = superType;
	}
}
