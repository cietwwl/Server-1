package com.playerdata.groupcompetition.util;

public class GCompTips {

	private static String tipsNotSelectionStageNow = "当前不是海选阶段";
	private static String tipsNotMatchStageNow = "当前不是赛事阶段";
	private static String tipsEnterSelectionStage = "帮派争霸进入海选阶段!";
	private static String tipsEnterEventsStage = "帮派争霸进入赛事阶段!";
	private static String tipsEnterEventsType = "帮派争霸进入[%s]赛事！";
	
	public static String getTipsNotSelectionStageNow() {
		return tipsNotSelectionStageNow;
	}

	public static String getTipsNotMatchStageNow() {
		return tipsNotMatchStageNow;
	}
	
	public static String getTipsEnterSelectionStage() {
		return tipsEnterSelectionStage;
	}
	
	public static String getTipsEnterEventsStage() {
		return tipsEnterEventsStage;
	}
	
	public static String getTipsEnterEventsType(String name) {
		return String.format(tipsEnterEventsType, name);
	}
}
