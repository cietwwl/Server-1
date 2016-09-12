package com.rwbase.dao.groupcompetition.pojo;

import com.rw.fsutil.common.IReadOnlyPair;

public class GroupCompetitionStageCfg {

	private String stageName; // 阶段名字
	private int stageType; // 阶段类型
	private String startTime; // 开始时间，原始表达式
	private IReadOnlyPair<Integer, Integer> startTimeInfo; // 开始时间
	private int lastDays; // 持续的天数
	
	public String getStageName() {
		return stageName;
	}
	
	public int getStageType() {
		return stageType;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTimeInfo(IReadOnlyPair<Integer, Integer> value) {
		this.startTimeInfo = value;
	}
	
	public IReadOnlyPair<Integer, Integer> getStartTimeInfo() {
		return startTimeInfo;
	}
	
	public int getLastDays() {
		return lastDays;
	}
	
}
