package com.playerdata.groupcompetition.util;

public class GCompCommonConfig {

	private static int maxMemberCountOfTeam = 3;
	private static int maxMatchingLvFloating = 5; // 最大的匹配等级浮动区间

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
}
