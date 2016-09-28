package com.playerdata.groupcompetition.util;

import java.util.concurrent.TimeUnit;

public class GCompCommonConfig {

	private static int maxMemberCountOfTeam = 3;
	private static int maxMatchingLvFloating = 5; // 最大的匹配等级浮动区间
	private static int broadcastIntervalSeconds = 5; // 广播时效任务的间隔
	private static int matchingIntervalSeconds = 5; // 匹配的间隔
	private static int machingTimeoutMillis = (int)TimeUnit.SECONDS.toMillis(20); // 匹配超时的时间

	/**
	 * 
	 * 获取队伍最大的成员数量
	 * 
	 * @return
	 */
	public static int getMaxMemberCountOfTeam() {
		return maxMemberCountOfTeam;
	}
	
	/**
	 * 
	 * 获取最大的匹配等级浮动区间
	 * 
	 * @return
	 */
	public static int getMaxMatchingLvFloating() {
		return maxMatchingLvFloating;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getBroadcastIntervalSeconds() {
		return broadcastIntervalSeconds;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getMatchingIntervalSeconds() {
		return matchingIntervalSeconds;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getMachingTimeoutMillis() {
		return machingTimeoutMillis;
	}
}
