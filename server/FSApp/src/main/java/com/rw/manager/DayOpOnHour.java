package com.rw.manager;

import java.util.Calendar;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.DateUtils;


public class DayOpOnHour {

	private Calendar lastExecuteFinishTime;
	
	//执行小时（24小时制）
	private int hourOfDay;
	
	private int opMinute = 0;
	
	private ITimeOp timeOp;

	private boolean isRunning;
	
	public DayOpOnHour(ITimeOp timeOpP, int hourOfDay24P){
		this(timeOpP, hourOfDay24P, 0);
	}
	
	public DayOpOnHour(ITimeOp timeOpP, int hourOfDay24P, int minute){
		timeOp = timeOpP;
		hourOfDay = hourOfDay24P;
		opMinute = minute;
//		lastExecuteFinishTime = DateUtils.getCalendar(System.currentTimeMillis());
	}	
	
	public synchronized void tryRun(){
		if(isRunning || !isOpTime()){
			return;
		}
		
		if(lastExecuteFinishTime==null || DateUtils.dayChanged(lastExecuteFinishTime)){
			isRunning = true;
			try {
				System.out.println("执行小时任务："+hourOfDay+","+opMinute);
				timeOp.doTask();
			} catch (Exception e) {
				GameLog.error(LogModule.COMMON.getName(), "DayOpOnHour", "DayOpOnHour[tryRun]", e);
			} finally {
				lastExecuteFinishTime = DateUtils.getCalendar(System.currentTimeMillis());
				isRunning=false;
			}			
		}
	}
	
	private boolean isOpTime(){
		Calendar current = DateUtils.getCurrent();
		int currentHour = current.get(Calendar.HOUR_OF_DAY);
		int currentMin = current.get(Calendar.MINUTE);
		boolean isOpHour = currentHour >= hourOfDay;
		boolean isOpMin = currentMin >= opMinute;
		return isOpHour && isOpMin;
		
	}
	
}

