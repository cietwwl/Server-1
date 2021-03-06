package com.bm.worldBoss.cfg;

import java.util.Calendar;

import com.rw.fsutil.util.DateUtils;



public class WBCfg {

	private String id;
	
	private String preStartTimeStr; //可以进入的时间
	
	private String startTimeStr;	//开始时间 格式 9:30
	
	private String endTimeStr;		//结束时间 格式 9:30
	
	private String finishTimeStr; //整整结束的时间
	
	private String copyId;	//对应怪物的id
	
	private int weekDay;			//对应有效的星期
	
	private String killAttackAwardId;//最后一击奖励
	private String killAttackAward;//最后一击奖励
	
	

	public WBCfg() {
	}
	
	public WBCfg(WBCfg cfg){
		this.id = cfg.id;
		this.preStartTimeStr = cfg.preStartTimeStr;
		this.startTimeStr = cfg.preStartTimeStr;
		this.endTimeStr = cfg.endTimeStr;
		this.finishTimeStr = cfg.finishTimeStr;
		this.copyId = cfg.copyId;
		this.weekDay = cfg.weekDay;
		this.killAttackAward = cfg.killAttackAward;
		this.killAttackAwardId = cfg.killAttackAwardId;
	}

	public String getId() {
		return id;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}


	public String getCopyId() {
		return copyId;
	}

	public int getWeekDay() {
		return weekDay;
	}
	
	public long getStartTime(){
		return getTime(startTimeStr);
	}

	public long getEndTime(){
		return getTime(endTimeStr);
	}
	
	public long getPreStartTime(){
		return getTime(preStartTimeStr);
	}
	
	public long getFinishTime(){
		return getTime(finishTimeStr);
	}
		
	
	public String getPreStartTimeStr() {
		return preStartTimeStr;
	}

	public String getFinishTimeStr() {
		return finishTimeStr;
	}

	

	public String getKillAttackAwardId() {
		return killAttackAwardId;
	}

	public String getKillAttackAward() {
		return killAttackAward;
	}
	
	

	
	private long getTime(String cfgTime) {

		String[] split = cfgTime.split(":");
		int hour = Integer.parseInt(split[0]);
		int minute = Integer.parseInt(split[1]);

		Calendar current = DateUtils.getCurrent();
		current.set(Calendar.HOUR_OF_DAY, hour);
		current.set(Calendar.MINUTE, minute);
		current.set(Calendar.SECOND, 0);
		current.set(Calendar.MILLISECOND, 0);

		long time = current.getTimeInMillis();

		return time;
	}
	
	public static void main(String[] args) {
//		String tt = "20:30";
//		WBCfg cfg = new WBCfg();
//		long t = cfg.getTime(tt);
		String timeStr = DateUtils.getDateTimeFormatString(1477365300000L, "yyyy-MM-dd HH:mm");
		System.out.println(timeStr);
		
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPreStartTimeStr(String preStartTimeStr) {
		this.preStartTimeStr = preStartTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public void setFinishTimeStr(String finishTimeStr) {
		this.finishTimeStr = finishTimeStr;
	}

	public void setCopyId(String copyId) {
		this.copyId = copyId;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public void setKillAttackAwardId(String killAttackAwardId) {
		this.killAttackAwardId = killAttackAwardId;
	}

	public void setKillAttackAward(String killAttackAward) {
		this.killAttackAward = killAttackAward;
	}

	
}
