package com.rw.fsutil.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.common.SimpleThreadFactory;

public class DateUtils {

	private static ThreadLocal<SimpleDateFormat> formate_ddhhmmss = new ThreadLocal<SimpleDateFormat>();
	private static ThreadLocal<SimpleDateFormat> formate_hhmmss = new ThreadLocal<SimpleDateFormat>();
	private static ThreadLocal<SimpleDateFormat> formate_yyyyMMddHHmm = new ThreadLocal<SimpleDateFormat>();
	private static ThreadLocal<Calendar> currentCalendar = new ThreadLocal<Calendar>();
	
	public static long DayTime = 24 * 60 * 60 * 1000l;
	
	private static volatile long secondLevelMillis;

	static {
		secondLevelMillis = System.currentTimeMillis();
		Executors.newScheduledThreadPool(1, new SimpleThreadFactory("seconds")).scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				secondLevelMillis = System.currentTimeMillis();
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	/**
	 * 获取表示当前时间的毫秒数，秒级别精度
	 * @return
	 */
	public static long getSecondLevelMillis() {
		return secondLevelMillis;
	}

	public static Calendar getCalendar() {
		Calendar current = currentCalendar.get();
		if (current == null) {
			current = Calendar.getInstance();
			currentCalendar.set(current);
		}
		return current;
	}

	public static Calendar getCurrent() {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar;
	}

	public static int getCurrentHour() {
		
		return getCurrent().get(Calendar.HOUR_OF_DAY);
	}

	public static int getCurMinuteOfHour() {
		return getCurrent().get(Calendar.MINUTE);
	}
	
	public static int getCurrentDayOfYear() {
		return getCurrent().get(Calendar.DAY_OF_YEAR);
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
		Calendar cal = getCalendar();
		cal.setTimeInMillis(now);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	/** 玩家的5点刷新方法;从player类移过来0.0发现左了无用功 */
	public static boolean isNewDayHour(int hour, long lastResetTime) {
		return getCurrentHour() >= hour && dayChanged(lastResetTime);
	}

	/** 传入时间，返回小时，问下同事是否有重复的 */
	public static int getinHour(long lastTime) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(lastTime);
		int tmp = calendar.get(Calendar.HOUR_OF_DAY);
		return tmp;
	}

	public static boolean dayChanged(long timeStmp) {
		Calendar currentDay = getCalendar(timeStmp);
		long now = System.currentTimeMillis();
		int change = (int) (now - timeStmp);
		return dayChanged(currentDay);
	}

	public static boolean dayChanged(Calendar dayFlag) {
		Calendar currentDay = Calendar.getInstance();
		int year = currentDay.get(Calendar.YEAR);
		int dayOfYear = currentDay.get(Calendar.DAY_OF_YEAR);
		int yearLast = dayFlag.get(Calendar.YEAR);
		int dayOfYearLast = dayFlag.get(Calendar.DAY_OF_YEAR);
		// System.out.println("dateutils.year" + year +" dayofyear" + dayOfYear
		// +" yearlast +" + yearLast + " dayofyearlast" + dayOfYearLast);
		if (year > yearLast) {
			return true;
		}

		if (year == yearLast && dayOfYear > dayOfYearLast) {
			return true;
		}
		return false;
	}

	/** 以5点为界限，距离开始时间的间隔天数；需靠考虑策划填表习惯 */
	public static int getDayLimitHour(int hour, long earlyTime) {
		if (getinHour(earlyTime) < hour) {
			if (getCurrentHour() >= hour) {
				return getDayDistance(earlyTime, System.currentTimeMillis()) + 1;
			} else {
				int tmp = getDayDistance(earlyTime, System.currentTimeMillis());
				return tmp < 0 ? 0 : tmp;
			}
		} else {
			if (getCurrentHour() >= hour) {
				return getDayDistance(earlyTime, System.currentTimeMillis());
			} else {
				int tmp = getDayDistance(earlyTime, System.currentTimeMillis()) - 1;
				return tmp < 0 ? 0 : tmp;
			}
		}
	}

	public static boolean isTheSameDayOfWeek(int dayOfWeek) {
		return isTheSameDayOfWeekAndHour(dayOfWeek, 0);
	}
	
	
	
	
	/**
	 * <pre>
	 * 是否是一周的同一天，并且开启的小时相同
	 * <b>以下内容一定要注意：
	 * 【注】如果当前的小时并没有超过给定的小时，就不会更新到是这周的某一天
	 * E.g,传入的dayOfWeek是5(周五的意思)，hour是5点
	 * 当前时间是2016-04-22这天是周五。给定的hour是5点，然而当前是4点
	 * 那么方法里会自动判定今天还是4月21日周四。
	 * </b>
	 * </pre>
	 * 
	 * @param dayOfWeek
	 * @param hour
	 *            <b>一定是24小时制</b> 如果当前小时已经超过或者大于传递的小时，就当作相同，返回true
	 * @return
	 */
	public static boolean isTheSameDayOfWeekAndHour(int dayOfWeek, int hour) {
		hour = hour >= 24 ? 0 : hour;// 不能超过24点，24点自动判定为0点
		Calendar currentDay = getCurrent();
		int day = currentDay.get(Calendar.DAY_OF_WEEK);// 当前天数
		int curHour = currentDay.get(Calendar.HOUR_OF_DAY);// 当前小时

		boolean isHourTrue = curHour >= hour;

		if (day == Calendar.SUNDAY) {// 默认周末为第一天值为1
			day = 7;
		} else {
			day -= 1;
		}

		if (!isHourTrue) {
			day -= 1;
			if (day == 0) {
				day = 7;
			}

			isHourTrue = true;
		}

		if (day == dayOfWeek && isHourTrue) {// 时间超过了重置点&&是一周的同一天
			return true;
		}

		return false;
	}

	public static Calendar getCalendar(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	
	/**
	 * 计算当前这周的某一天的时间点
	 * @param dayOfWeek 一周里的某一天，周日为1，以此类推
	 * @param hour 一天里的某一小时，24小时制
	 * @param delayNextWeek 当前时间如果超过目标时间是否顺延到下一周
	 * @return
	 */
	public static long getTargetDayOfWeekTimeMils(int dayOfWeek, int hour, boolean delayNextWeek){
		Calendar calendar = getCurrent();
		calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long millis = calendar.getTimeInMillis();
		if(millis < System.currentTimeMillis() && delayNextWeek){
			//已经超时，则顺延为计算下一周的时间点
			Calendar c2 = getCalendar();
			c2.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1);
			c2.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			c2.set(Calendar.HOUR_OF_DAY, hour);
			c2.set(Calendar.MINUTE, 0);
			c2.set(Calendar.SECOND, 0);
			millis = c2.getTimeInMillis();
		}
		return millis;
	}

	/**
	 * 是否是重置数据时间点<br/>
	 * <b>这个方法只适用于必须是每天都重置的</b>
	 * 
	 * @param hour
	 *            重置时间的触发小时，必须是24小时制
	 * @param minute
	 *            重置时间的触发分钟
	 * @param second
	 *            重置时间的触发秒
	 * @param lastTime
	 *            上次重置的时间
	 * @return
	 */
	public static boolean isResetTime(int hour, int minute, int second, long lastTime) {
		return isResetTime(hour, minute, second, lastTime, DAY_MILLIS);
	}

	/**
	 * 是否是重置数据时间点<br/>
	 * <b>这个方法可以用于循环多少天重置</b>
	 * 
	 * @param hour
	 *            重置时间的触发小时，必须是24小时制
	 * @param minute
	 *            重置时间的触发分钟
	 * @param second
	 *            重置时间的触发秒
	 * @param lastTime
	 *            上次重置的时间
	 * @param offTimeMillis
	 *            重置的循环时间点
	 * @return
	 */
	public static boolean isResetTime(int hour, int minute, int second, long lastTime, long offTimeMillis) {
		Calendar calendar = getCalendar();
		// long curTime = calendar.getTimeInMillis();// 当前时间
		long curTime = System.currentTimeMillis();
		calendar.setTimeInMillis(curTime);
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
	 * 获取指定时间最近一次重置的毫秒数
	 * 
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getResetTime(int hour, int minute, int second) {
		Calendar calendar = getCalendar();
		// long curTime = calendar.getTimeInMillis();// 当前时间
		long curTime = System.currentTimeMillis();
		calendar.setTimeInMillis(curTime);
		// 重置时间
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		long resetTimeMillis = calendar.getTimeInMillis();// 今天重置点时间
		if (curTime < resetTimeMillis) {// 当前登录时间小于今天重置点时间，那么就要重置判断时间点再推前一天
			resetTimeMillis -= DAY_MILLIS;
		}
		return resetTimeMillis;
	}

	/**
	 * 获取当天5点的重置时间点
	 * @return
	 */
	public static long getCurrentDayResetTime(){
		Calendar c = getCalendar();
		long curTime = System.currentTimeMillis();
		c.setTimeInMillis(curTime);
		// 重置时间
		c.set(Calendar.HOUR_OF_DAY, 5);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	
	/**
	 * 相隔的天数,因为都设置为了0的时分秒，所以是相对意义上的
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
	 * 相隔的绝对小时数
	 * 
	 * @param earyDay
	 * @param lateDay
	 * @return
	 */
	public static int getAbsoluteHourDistance(long earyDay, long lateDay) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(earyDay);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(lateDay);

		long timeInMillis = c1.getTimeInMillis();
		long timeInMillis2 = c2.getTimeInMillis();

		long distanceTime = Math.abs(timeInMillis2 - timeInMillis);
		int distance = (int) (distanceTime / (60 * 60 * 1000));
		return distance;
	}

	/**
	 * 传入yyyyMMddhhmm格式的日期字符串转换为毫秒
	 * 
	 * @param earyDay
	 * @param lateDay
	 * @return
	 */
	public static long YyyymmddhhmmToMillionseconds(String str) {
		try {
			long millionseconds = getyyyyMMddHHmmFormater().parse(str).getTime();
			return millionseconds;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static SimpleDateFormat getyyyyMMddHHmmFormater() {
		SimpleDateFormat format = formate_yyyyMMddHHmm.get();
		if (format == null) {
			format = new SimpleDateFormat("yyyyMMddHHmm");
			formate_yyyyMMddHHmm.set(format);
		}
		return format;
	}

	public static SimpleDateFormat getddHHmmFormater() {
		SimpleDateFormat format = formate_ddhhmmss.get();
		if (format == null) {
			format = new SimpleDateFormat("dd HH:mm:ss");
			formate_ddhhmmss.set(format);
		}
		return format;
	}
	
	public static SimpleDateFormat getHHmmFormater() {
		SimpleDateFormat format = formate_hhmmss.get();
		if (format == null) {
			format = new SimpleDateFormat("HH:mm:ss");
			formate_hhmmss.set(format);
		}
		return format;
	}
	
	public static String getTimeOfDayFomrateTips(long time){
		return getTimeOfDayFormater().format(new Date(time));
	}
	
	public static SimpleDateFormat getTimeOfDayFormater() {
		SimpleDateFormat format = formate_hhmmss.get();
		if (format == null) {
			format = new SimpleDateFormat("HHmmss");
			formate_hhmmss.set(format);
		}
		return format;
	}
	
	public static String getHHMMSSFomrateTips(){
		return getHHmmFormater().format(new Date(System.currentTimeMillis()));
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
		Calendar c = getCalendar();
		c.setTimeInMillis(time);
		setDayZeroTime(c);
		return c.getTimeInMillis();
	}

	/**
	 * 获取时间的0点
	 * 
	 * @param time
	 * @return
	 */
	public static Calendar getDayZeroCalendar(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setDayZeroTime(c);
		return c;
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
	
	/***
	 * 获取时间指定格式字符串
	 * 
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getDateTimeFormatString(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar instance = Calendar.getInstance();
		return simpleDateFormat.format(instance.getTime());
	}
	
	/**
	 * 根据字符串获取时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static long getDateTimeFormatTime(String time, String format, int hour) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date dateTime;
		try {
			dateTime = simpleDateFormat.parse(time);

			long time2 = dateTime.getTime();
			return getHour(time2, hour);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static String timeToCountDown(long time) {
		int hour = (int) (time / (60 * 60 * 1000));
		int minute = (int) ((time - hour * 60 * 60 * 1000) / (60 * 1000));
		int second = (int) ((time - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000);
		return hour + ":" + minute + ":" + second;
	}

}
