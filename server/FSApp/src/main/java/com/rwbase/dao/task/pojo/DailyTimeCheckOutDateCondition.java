package com.rwbase.dao.task.pojo;

import java.util.Calendar;

import com.rwbase.dao.task.DailyCheckOutDateCondition;

public class DailyTimeCheckOutDateCondition implements DailyCheckOutDateCondition {

	private final int endHour;
	private final int endMinute;
	
	public DailyTimeCheckOutDateCondition(String endTimeInfo) {
		String[] timeInfo = endTimeInfo.split("_")[1].split(":");
		endHour = Integer.parseInt(timeInfo[0]);
		endMinute = Integer.parseInt(timeInfo[1]);
	}

	@Override
	public boolean isOutDate() {
		Calendar instance = Calendar.getInstance();
		int nowHour = instance.get(Calendar.HOUR_OF_DAY);
		int nowMinute = instance.get(Calendar.MINUTE);
		return endHour < nowHour || (endHour == nowHour && endMinute < nowMinute);
	}
}
