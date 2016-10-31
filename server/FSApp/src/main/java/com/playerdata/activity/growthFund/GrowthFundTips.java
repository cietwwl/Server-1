package com.playerdata.activity.growthFund;

public class GrowthFundTips {

	private static String tipsAlreadyBought = "您已经购买过成长基金礼包了，无需重复购买哦！";
	private static String tipsVipLvNotReach = "贵族等级到达%d级才能购买成长基金礼包！";
	private static String tipsLvNotReach = "等级到达%d级才能购买成长基金礼包！";
	private static String tipsDiamondNotEnough = "钻石不足，无法购买！";
	private static String tipsYouHaveNotBought = "您并没有购买成长基金礼包，不能领取奖励！";
	private static String tipsLvNotReachToGet = "等级需要达到%d才能领取该奖励！";
	private static String tipsNoSuchItem = "不存在该奖项！";
	private static String tipsAlreadyGot = "您已经领取该奖励了！";
	private static String tipsAlreadyBoughtCountNotReached = "购买人数需要达到%d人才能领取该奖励";

	public static String getTipsAlreadyBought() {
		return tipsAlreadyBought;
	}

	public static String getTipsVipLvNotReach(int vipLv) {
		return String.format(tipsVipLvNotReach, vipLv);
	}

	public static String getTipsLvNotReach(int lv) {
		return String.format(tipsLvNotReach, lv);
	}

	public static String getTipsDiamondNotEnough() {
		return tipsDiamondNotEnough;
	}

	public static String getTipsYouHaveNotBought() {
		return tipsYouHaveNotBought;
	}

	public static String getTipsLvNotReachToGet(int lv) {
		return String.format(tipsLvNotReachToGet, lv);
	}

	public static String getTipsNoSuchItem() {
		return tipsNoSuchItem;
	}

	public static String getTipsAlreadyGot() {
		return tipsAlreadyGot;
	}

	public static String getTipsAlreadyBoughtCountNotReached(int count) {
		return String.format(tipsAlreadyBoughtCountNotReached, count);
	}
}
