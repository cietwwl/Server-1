package com.rw.service.Privilege.datamodel;

import com.log.GameLog;

public class ChargeTypePriority {
	private static ChargeTypePriority instance;
	public static ChargeTypePriority getShareInstance(){
		if (instance == null){
			instance = new ChargeTypePriority();
		}
		return instance;
	}
	/**
	 * hard code 充值类型的优先级判断：月卡优先于vip
	 * @param leftChargeType
	 * @param rightChargeType
	 * @return
	 */
	public boolean gt(String leftChargeType, String rightChargeType) {
		boolean leftIsVip = leftChargeType.startsWith("vip");
		boolean rightIsVip = rightChargeType.startsWith("vip");
		boolean leftIsMonth = leftChargeType.startsWith("month");
		boolean rightIsMonth = rightChargeType.startsWith("month");

		if (leftIsVip && rightIsMonth){
			return false;
		}
		
		if (leftIsMonth && rightIsVip){
			return true;
		}
		
		if (leftIsVip && rightIsVip){
			int leftVip = extractVipLevel(leftChargeType);
			int rightVip = extractVipLevel(rightChargeType);
			return leftVip > rightVip;
		}
		
		if (leftIsMonth && rightIsMonth){
			int leftMonthLevel = extractMonthLevel(leftChargeType);
			int rightMonthLevel = extractMonthLevel(rightChargeType);
			return leftMonthLevel > rightMonthLevel;
		}
		
		GameLog.error("特权", "计算充值类型优先级", "未知充值类型:"+leftChargeType+","+rightChargeType);
		return false;
	}

	private int extractMonthLevel(String chargeTy) {
		String monthVal = chargeTy.substring(chargeTy.indexOf("month")+5);
		if ("normal".equals(monthVal)){
			return 0;
		}
		if ("vip".equals(monthVal)){
			return 1;
		}
		
		return -1;
	}

	private int extractVipLevel(String chargeTy) {
		return Integer.parseInt(chargeTy.substring(chargeTy.indexOf("vip")+3));
	}
}
