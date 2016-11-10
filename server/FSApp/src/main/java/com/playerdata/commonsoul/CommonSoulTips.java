package com.playerdata.commonsoul;

public class CommonSoulTips {

	private static String tipsExchangeNotOpened = "该兑换没有开放！";
	private static String tipsItemNotEnough = "[%s]数量不足[%s]";
	private static String tipsNotReachExchangeRate = "最少需要[%s]个才能兑换";

	public static String getTipsExchangeNotOpened() {
		return tipsExchangeNotOpened;
	}
	
	public static String getTipsItemNotEnough(String itemName, int count) {
		return String.format(tipsItemNotEnough, itemName, count);
	}

	public static String getTipsNotReachExchangeRate(int count) {
		return String.format(tipsNotReachExchangeRate, count);
	}
}
