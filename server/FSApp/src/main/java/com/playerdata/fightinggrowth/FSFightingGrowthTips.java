package com.playerdata.fightinggrowth;

public class FSFightingGrowthTips {

	private static String tipsFightingNotReached = "晋级需要战斗力达到：%s";
	private static String tipsItemNotEnough = "材料[%s]数量需要达到%s个才能晋级";
	private static String tipsFightingGrowthIsMax = "您已经晋级到最高等级了！";
	private static String tipsConsumeItemFail = "扣取升级材料失败！";
	private static String tipsUpgradeTitleSuccess = "战力称号晋级成功！";
	private static String tipsUpgradeRewardMailTitle = "战力晋级奖励";
	private static String tipsUpgradeRewardMailContent = "恭喜你晋级到[%s]，以下是你的晋级奖励！";
	
	public static String getTipsFightingNotReached(int fightingRequired) {
		return String.format(tipsFightingNotReached, fightingRequired);
	}
	
	public static String getTipsItemNotEnough(String itemName, int count) {
		return String.format(tipsItemNotEnough, itemName, count);
	}
	
	public static String getTipsFightingGrowthIsMax() {
		return tipsFightingGrowthIsMax;
	}
	
	public static String getTipsConsumeItemFail() {
		return tipsConsumeItemFail;
	}
	
	public static String getTipsUpgradeTitleSuccess() {
		return tipsUpgradeTitleSuccess;
	}
	
	public static String getTipsUpgradeRewardMailTitle() {
		return tipsUpgradeRewardMailTitle;
	}
	
	public static String getTipsUpgradeRewardMailContent(String title) {
		return String.format(tipsUpgradeRewardMailContent, title);
	}
}
