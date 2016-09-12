package com.bm.worldBoss.cfg;

import com.rw.fsutil.util.DateUtils;



public class WBCfg {

	private String id;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String monsterCfgId;
	
	private int weekDay;
	
	private String killAttackAwardId;//最后一击奖励

	public String getId() {
		return id;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public String getMonsterCfgId() {
		return monsterCfgId;
	}

	public int getWeekDay() {
		return weekDay;
	}
	
	public long getStartTime(){
		return getTime(this.startTimeStr);
	}

	public long getEndTime(){
		return getTime(this.endTimeStr);
	}
	
	private long getTime(String cfgTime) {
		
		String[] split = cfgTime.split(":");
		int hour = Integer.parseInt(split[0]);
		int minute = Integer.parseInt(split[1]);
		
		long startTime = DateUtils.getResetTime(hour, minute, 0);
		
		return startTime;
	}

	public String getKillAttackAwardId() {
		return killAttackAwardId;
	}
	
	
	

	
	
	
}
