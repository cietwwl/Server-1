package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompStageType;

@SynClass
public class GCompBaseInfo {

	private boolean isStart; // 帮战是否已经开始了
	private GCompEventsStatus eventStatus = GCompEventsStatus.NONE; // 当前的赛事状态
	private GCompStageType currentStageType = GCompStageType.EMPTY; // 当前的阶段状态
	private long leftTime; // 帮战开始的剩余时间
	@IgnoreSynField
	private long startTime; // 本次帮战的开始时间
	
	public boolean isStart() {
		return this.isStart;
	}
	
	public void setStart(boolean flag) {
		this.isStart = flag;
	}
	
	public GCompEventsStatus getEventStatus() {
		return this.eventStatus;
	}
	
	public void setEventStatus(GCompEventsStatus status) {
		this.eventStatus = status;
	}
	
	public GCompStageType getCurrentStageType() {
		return currentStageType;
	}
	
	public void setCurrentStageType(GCompStageType type) {
		this.currentStageType = type;
	}
	
	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long value) {
		this.leftTime = value;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long pStartTime) {
		this.startTime = pStartTime;
	}
}
