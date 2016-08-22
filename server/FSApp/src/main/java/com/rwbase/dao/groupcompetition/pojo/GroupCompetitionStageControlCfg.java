package com.rwbase.dao.groupcompetition.pojo;

import java.util.Collections;
import java.util.List;

import com.rw.fsutil.common.IReadOnlyPair;

public class GroupCompetitionStageControlCfg {

	private int planId;
	private int startType;
	private int startWeeks;
	private int startDayOfWeek;
	private String startTime;
	private String stageDetail;
	private IReadOnlyPair<Integer, Integer> startTimeInfo;
	private List<Integer> stageDetailList;
	
	public int getPlanId() {
		return planId;
	}
	
	public int getStartType() {
		return startType;
	}
	
	public int getStartWeeks() {
		return startWeeks;
	}
	
	public int getStartDayOfWeek() {
		return startDayOfWeek;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getStageDetail() {
		return stageDetail;
	}
	
	public void setStartTimeInfo(IReadOnlyPair<Integer, Integer> pValue) {
		this.startTimeInfo = pValue;
	}
	
	public IReadOnlyPair<Integer, Integer> getStartTimeInfo() {
		return startTimeInfo;
	}
	
	public void setStageDetailList(List<Integer> list) {
		this.stageDetailList = Collections.unmodifiableList(list);
	}
	
	public List<Integer> getStageDetailList() {
		return stageDetailList;
	}
	
}
