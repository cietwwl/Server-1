package com.playerdata.activityCommon;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;


/**
 * 活动时间的转化类
 * @author aken
 *
 */
public class ActivityTimeHelper {
	
	private static int TIME_SIZE = "YYYYMMDDHHmm".length();	//绝对时间的长度
	private static int MAX_DAYS_SIZE = "9999".length();	//相对时间的长度
	private static long ONE_DAY_MS = 24 * 60 * 60 * 1000;	//一天的毫秒数
	
	public static int RESET_HOUR = 5;	//活动的重置时间
	private static long openTime = 0;	//开服时间，起服后从登录服取的，会有延时
	
	/**
	 * 把时间转化成毫秒
	 * <li>如果是相对时间，先转化成绝对时间</li>
	 * @param startTime
	 * @param endTime
	 * @return TimePair 返回这个结构，是需要替换原来的时间字符串的，前端只能解析绝对时间
	 */
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
