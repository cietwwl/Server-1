package com.rwbase.dao.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @author HC
 * @date 2016年1月16日 下午2:36:25
 * @Description 帮派的通用工具类
 */
public class GroupUtils {

	/**
	 * <pre>
	 * 判断文字的长度
	 * 要求中文和英文
	 * </pre>
	 * 
	 * @param name
	 * @return 如果返回-1就代表有不允许通过的字符
	 */
	public static int getChineseNumLimitLength(String name) {
		int nameLength = 0;

		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (c > 127) {
				nameLength += 2;
			} else if (c >= 'a' && c <= 'z') {
				nameLength += 1;
			} else if (c >= 'A' && c <= 'Z') {
				nameLength += 1;
			} else if (c >= '0' && c <= '9') {
				nameLength += 1;
			} else {
				return -1;
			}
		}

		return nameLength;
	}

	/**
	 * 单纯这里只是把字符串所在的字节计算出来，按照1比2的比例
	 * 
	 * @param content
	 * @return
	 */
	public static int getContentLength(String content) {
		int nameLength = 0;

		int len = content.length();
		for (int i = 0; i < len; i++) {
			char c = content.charAt(i);
			if (c > 127) {
				nameLength += 2;
			} else {
				nameLength += 1;
			}
		}

		return nameLength;
	}

	/**
	 * 获取一个乱序的索引列表
	 * 
	 * @param size
	 * @return
	 */
	public static List<Integer> getShuffleIndexList(int size) {
		List<Integer> indexArr = new ArrayList<Integer>(size);
		for (int i = size - 1; i >= 0; --i) {
			indexArr.add(i);
		}
		Collections.shuffle(indexArr);// 打乱顺序
		return indexArr;
	}

	/**
	 * 退出时间
	 * 
	 * @param now
	 * @param quitTime
	 * @param needCoolingTime
	 * @return
	 */
	public static String quitGroupTimeTip(long now, long quitTime, long needCoolingTime) {
		long offTime = now - quitTime;
		if (offTime <= 0 || offTime >= needCoolingTime) {
			return "";
		}

		long leftTime = needCoolingTime - offTime;
		if (leftTime <= 0) {
			return "";
		}

		return millisTimeParse2Str(leftTime);
	}

	private static final long ONE_SECOND_MILLIS = 1000l;// 一秒的毫秒
	private static final long ONE_MINUTE_MILLIS = 60 * ONE_SECOND_MILLIS;// 一分钟的毫秒
	private static final long ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS;// 一小时的毫秒
	private static final long ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS;// 一天时间毫秒

	/**
	 * 转换MillisTime到文字
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String millisTimeParse2Str(long timeMillis) {
		if (timeMillis <= 0) {
			return "";
		}

		long days = timeMillis / ONE_DAY_MILLIS;

		long leftHourMillis = timeMillis % ONE_DAY_MILLIS;
		long hours = leftHourMillis / ONE_HOUR_MILLIS;

		long leftMinuteMillis = leftHourMillis % ONE_HOUR_MILLIS;
		long minutes = leftMinuteMillis / ONE_MINUTE_MILLIS;

		long leftSecondMillis = leftMinuteMillis % ONE_MINUTE_MILLIS;
		long second = leftSecondMillis / ONE_SECOND_MILLIS;

		StringBuilder sb = new StringBuilder();
		if (days > 0) {
			sb.append(days).append("天");
		}

		if (hours > 0) {
			sb.append(hours).append("小时");
		}

		if (minutes > 0) {
			sb.append(minutes).append("分");
		}

		if (days <= 0 && hours <= 0 && second > 0) {
			sb.append(second).append("秒");
		}

		return sb.toString();
	}
}