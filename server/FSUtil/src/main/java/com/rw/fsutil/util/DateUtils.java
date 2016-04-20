package com.rw.fsutil.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
	
	private static SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");

	public static Calendar getCurrent() {
		return Calendar.getInstance();
	}

	public static int getCurrentHour() {

		return getCurrent().get(Calendar.HOUR_OF_DAY);
	}

	private static final long DAY_MILLIS = TimeUnit.DAYS.toMillis(1);// 1天的毫秒数

	public static String getDateStr(Date date) {
		return getDateFormat().format(date);
	}

	public static String getYesterdayDateStr() {
		return getDateStr(-1);
	}

	public static String getDateStr(int dayBefore) {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_YEAR, dayBefore);
		return getDateFormat().format(instance.getTime());
	}

	public static long getDateTime(int dayBefore) {
		String dateStr = getDateStr(dayBefore);
		return getTime(dateStr);
	}

	public static long getDateTime(long timeInMili) {
		String dateStr = getDateFormat().format(new Date(timeInMili));
		return getTime(dateStr);
	}

	public static long getTime(String dateStr) {
		long time = 0;
		try {
			Date date = getDateFormat().parse(dateStr);
			time = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	public static long getNowHour() {
		return getHour(0);
	}

	public static long getHour(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.HOUR, offset);
		return calendar.getTimeInMillis();
	}

	public static long getHour(long now, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(now));
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime();
	}

	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static boolean dayChanged(long timeStmp) {
		Calendar currentDay = getCalendar(timeStmp);
		return dayChanged(currentDay);
	}

	public static boolean dayChanged(Calendar dayFlag) {
		Calendar currentDay = Calendar.getInstance();
		int year = currentDay.get(Calendar.YEAR);
		int dayOfYear = currentDay.get(Calendar.DAY_OF_YEAR);
		int yearLast = dayFlag.get(Calendar.YEAR);
		int dayOfYearLast = dayFlag.get(Calendar.DAY_OF_YEAR);
//		System.out.println("dateutils.year" + year +" dayofyear" + dayOfYear +" yearlast +" + yearLast + " dayofyearlast" + dayOfYearLast);
		if (year > yearLast) {
			return true;
		}

		if (year == yearLast && dayOfYear > dayOfYearLast) {
			return true;
		}
		return false;
	}

	public static boolean isTheSameDayOfWeek(int dayOfWeek) {
		Calendar currentDay = getCurrent();
		return currentDay.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
	}

	public static Calendar getCalendar(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	/**
	 * 是否是重置数据时间点<br/>
	 * <b>这个方法只适用于必须是每天都重置的</b>
	 * 
	 * @param hour 重置时间的触发小时，必须是24小时制
	 * @param minute 重置时间的触发分钟
	 * @param second 重置时间的触发秒
	 * @param lastTime 上次重置的时间
	 * @return
	 */
	public static boolean isResetTime(int hour, int minute, int second, long lastTime) {
		return isResetTime(hour, minute, second, lastTime, DAY_MILLIS);
	}

	/**
	 * 是否是重置数据时间点<br/>
	 * <b>这个方法可以用于循环多少天重置</b>
	 * 
	 * @param hour 重置时间的触发小时，必须是24小时制
	 * @param minute 重置时间的触发分钟
	 * @param second 重置时间的触发秒
	 * @param lastTime 上次重置的时间
	 * @param offTimeMillis 重置的循环时间点
	 * @return
	 */
	public static boolean isResetTime(int hour, int minute, int second, long lastTime, long offTimeMillis) {
		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();// 当前时间

		// 重置时间
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		long resetTimeMillis = calendar.getTimeInMillis();// 今天重置点时间

		if (curTime < resetTimeMillis) {// 当前登录时间小于今天重置点时间，那么就要重置判断时间点再推前一天
			resetTimeMillis -= offTimeMillis;
		}
		return lastTime < resetTimeMillis;
	}

	/**
	 * 相隔的天数
	 * 
	 * @param earyDay
	 * @param lateDay
	 * @return
	 */
	public static int getDayDistance(long earyDay, long lateDay) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(earyDay);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(lateDay);

		setDayZeroTime(c1);
		setDayZeroTime(c2);

		long timeInMillis = c1.getTimeInMillis();
		long timeInMillis2 = c2.getTimeInMillis();

		long distanceTime = Math.abs(timeInMillis2 - timeInMillis);

		int distance = (int) (distanceTime / (24 * 60 * 60 * 1000));
		return distance;
	}
	/**
	 * 传入yyyyMMddhhmm格式的日期字符串转换为毫秒
	 * 
	 * @param earyDay
	 * @param lateDay
	 * @return
	 */
	public static long YyyymmddhhmmToMillionseconds(String str){
		try{
			long millionseconds = yyyyMMddHHmm.parse(str).getTime();
			return millionseconds;
		}catch(Exception e){
			
		}		
		return 0;
	}
	
	
	
	public static void setDayZeroTime(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 获取时间的0点
	 * 
	 * @param time
	 * @return
	 */
	public static long getDayZeroTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setDayZeroTime(c);
		return c.getTimeInMillis();
	}

	/***
	 * 获取时间指定格式字符串
	 * 
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getDateTimeFormatString(long time, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(time);
		return simpleDateFormat.format(instance.getTime());
	}

	public static void main(String[] args) throws ParseException {
		// System.out.println(new Date(getHour(System.currentTimeMillis(), 12)));
		// System.out.println(new Date(getHour(getDateTime(1), 9)));

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// long time = sdf.parse("2015-12-11 4:00:01").getTime();
		//
		// System.err.println(isResetTime(5, 0, 0, time));
	}
}
