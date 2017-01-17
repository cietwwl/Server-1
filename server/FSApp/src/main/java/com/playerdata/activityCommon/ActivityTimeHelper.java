package com.playerdata.activityCommon;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;


public class ActivityTimeHelper {
	
	private static int TIME_SIZE = "YYYYMMDDHHmm".length();
	private static int MAX_DAYS_SIZE = "9999".length();
	private static long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	
	public static int RESET_HOUR = 5;	//活动的重置时间
	private static long openTime = 0;
	
	public static TimePair transToAbsoluteTime(String startTime, String endTime){
		long startMil = cftStartTimeToLong(startTime);
		if(startMil <= 0) return null;
		long endMil = cftEndTimeToLong(startMil, endTime);
		TimePair tp = new TimePair();
		tp.setStartMil(startMil);
		tp.setEndMil(endMil);
		return tp;
	}
	
	private static long cftStartTimeToLong(String startTime){
		if(StringUtils.isBlank(startTime)) return 0;
		long result = 0;
		if(startTime.length() == TIME_SIZE){
			result = DateUtils.YyyymmddhhmmToMillionseconds(startTime);
		}else if(startTime.length() <= MAX_DAYS_SIZE){
			int afterOpenServerDay = Integer.parseInt(startTime);
			if(openTime <= 0){
				openTime = DateUtils.getHour(GameManager.getOpenTime(), RESET_HOUR);
				if(openTime <= 0){
					return 0;
				}
			}
			result = openTime + afterOpenServerDay * ONE_DAY_MS;
		}
		return result;
	}
	
	private static long cftEndTimeToLong(long startTime, String endTime){
		if(StringUtils.isBlank(endTime)) return Long.MAX_VALUE;
		long result = Long.MAX_VALUE;
		if(endTime.length() == TIME_SIZE){
			result = DateUtils.YyyymmddhhmmToMillionseconds(endTime);
		}else if(endTime.length() <= MAX_DAYS_SIZE){
			int afterStartDay = Integer.parseInt(endTime);
			result = startTime + afterStartDay * ONE_DAY_MS;
			result = DateUtils.getHour(result, RESET_HOUR);   //五点为重置时间
		}
		return result;
	}
	
	public static String getThisZoneTime(String totalTimeStr){
		if(null == totalTimeStr) return null;
		String zoneId = String.valueOf(GameManager.getZoneId());
		String tmpStr = totalTimeStr.replaceAll("\n\r", "");
		String[] splitStrArr = tmpStr.split("|");
		for(String zoneTimeStr : splitStrArr){
			if(zoneTimeStr.indexOf(zoneId) >= 0){
				String[] zoneAndTime = zoneTimeStr.split("_");
				if(zoneAndTime.length == 2){
					return zoneAndTime[1];
				}
			}
		}
		return null;
	}
	
	public static class TimePair{
		
		private String startTime;
		private String endTime;
		private long startMil;
		private long endMil;
		
		public String getStartTime() {
			return startTime;
		}
		
		public String getEndTime() {
			return endTime;
		}

		public long getStartMil() {
			return startMil;
		}

		public void setStartMil(long startMil) {
			this.startMil = startMil;
			this.startTime = DateUtils.getDateTimeFormatString(startMil, "yyyyMMddHHmm");
		}

		public long getEndMil() {
			return endMil;
		}

		public void setEndMil(long endMil) {
			this.endMil = endMil;
			this.endTime = DateUtils.getDateTimeFormatString(endMil, "yyyyMMddHHmm");
		}
	}
}
