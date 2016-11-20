package com.playerdata.activityCommon;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;


public class ActivityTimeHelper {
	
	private static int TIME_SIZE = "YYYYMMDDHHmm".length();
	private static int MAX_DAYS_SIZE = "9999".length();
	private static long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	
	private static int RESET_HOUR = 5;	//活动的重置时间
	
	public static long cftStartTimeToLong(String startTime){
		if(StringUtils.isBlank(startTime)) return 0;
		long result = 0;
		if(startTime.length() == TIME_SIZE){
			result = DateUtils.YyyymmddhhmmToMillionseconds(startTime);
		}else if(startTime.length() <= MAX_DAYS_SIZE){
			int afterOpenServerDay = Integer.parseInt(startTime);
			result = GameManager.getOpenTime() + afterOpenServerDay * ONE_DAY_MS;
			result = DateUtils.getHour(result, RESET_HOUR);   //五点为重置时间
		}
		return result;
	}
	
	public static long cftEndTimeToLong(long startTime, String endTime){
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
}
