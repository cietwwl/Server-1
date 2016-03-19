package com.rwbase.dao.task.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.common.enu.eTaskFinishDef;


@Table(name = "task_item")
@SynClass
public class TaskItem implements IMapItem{
	
	@Id
	private String id;
	private String userId;
	
	@CombineSave
	private int taskId;
	@CombineSave
	private eTaskFinishDef finishType;
	@CombineSave
	private int superType;
	@CombineSave
	private int drawState; //是否可以领取奖励,1可以，0不可以，2已领取
	@CombineSave
	private int curProgress; //当前的进度;
	@CombineSave
	private int totalProgress;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	

}
