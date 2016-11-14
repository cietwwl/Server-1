package com.playerdata.activityCommon;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;


public class ActivityTimeHelper {
	
	private static int TIME_SIZE = "YYYYMMDDHHmm".length();
	private static int MAX_DAYS_SIZE = "9999".length();
	private static long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	
	public static long cftStartTimeToLong(String startTime){
		if(StringUtils.isBlank(startTime)) return 0;
		if(startTime.length() == TIME_SIZE){
			return DateUtils.YyyymmddhhmmToMillionseconds(startTime);
		}
		if(startTime.length() <= MAX_DAYS_SIZE){
			int afterOpenServerDay = Integer.parseInt(startTime);
			return GameManager.getOpenTime() + afterOpenServerDay * ONE_DAY_MS;
		}
		return 0;
	}
	
	public static long cftEndTimeToLong(long startTime, String endTime){
		if(StringUtils.isBlank(endTime)) return Long.MAX_VALUE;
		if(endTime.length() == TIME_SIZE){
			return DateUtils.YyyymmddhhmmToMillionseconds(endTime);
		}
		if(endTime.length() <= MAX_DAYS_SIZE){
			int afterStartDay = Integer.parseInt(endTime);
			return startTime + afterStartDay * ONE_DAY_MS;
		}
		return Long.MAX_VALUE;
	}
}
