package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompStageType;

@SynClass
public class GCompBaseInfo {

	private boolean isStart; // 帮战是否已经开始了
	private GCompEventsStatus eventStatus = GCompEventsStatus.NONE; // 当前的赛事状态
	private GCompStageType currentStageType = GCompStageType.EMPTY; // 当前的阶段状态
	private long startTime; // 本次帮战的开始时间
	private long endTime; // 本阶段的结束时间
	
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
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long pStartTime) {
		this.startTime = pStartTime;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long pEndTime) {
		this.endTime = pEndTime;
	}

	@Override
	public String toString() {
		return "GCompBaseInfo [isStart=" + isStart + ", eventStatus=" + eventStatus + ", currentStageType=" + currentStageType + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
}
