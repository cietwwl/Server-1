package com.rwbase.dao.groupcompetition.pojo;

import java.util.Collections;
import java.util.List;

public class GroupCompetitionStageControlCfg {

	private int planId;
	private int startType;
	private int startWeeks;
	private int startDayOfWeek;
	private String stageDetail;
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
	
	public String getStageDetail() {
		return stageDetail;
	}
	
	public void setStageDetailList(List<Integer> list) {
		this.stageDetailList = Collections.unmodifiableList(list);
	}
	
	public List<Integer> getStageDetailList() {
		return stageDetailList;
	}
	
}
