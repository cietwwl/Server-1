package com.rwbase.dao.groupcompetition.pojo;

import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class GroupCompetitionStageCfg {

	private String cfgId;
	private String stageName; // 阶段名字
	private int stageType; // 阶段类型
	private String startTime; // 开始时间，原始表达式
	private String endTime; // 结束时间，原始表达式
	private IReadOnlyPair<Integer, Integer> startTimeInfo; // 开始时间
	private IReadOnlyPair<Integer, Integer> endTimeInfo; // 结束时间
	private int lastDays; // 持续的天数
	private boolean startImmediately; // 是否马上开始
	
	public String getCfgId() {
		return cfgId;
	}
	
	public String getStageName() {
		return stageName;
	}
	
	public int getStageType() {
		return stageType;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setStartTimeInfo(int hourOfDay, int minute) {
		this.startTimeInfo = Pair.CreateReadonly(hourOfDay, minute);
	}
	
	public void setEndTimeInfo(int hourOfDay, int minute) {
		this.endTimeInfo = Pair.CreateReadonly(hourOfDay, minute);
	}
	
	public IReadOnlyPair<Integer, Integer> getStartTimeInfo() {
		return startTimeInfo;
	}
	
	public IReadOnlyPair<Integer, Integer> getEndTimeInfo() {
		return endTimeInfo;
	}
	
	public int getLastDays() {
		return lastDays;
	}
	
	public boolean isStartImmediately() {
		return startImmediately;
	}
	
}
