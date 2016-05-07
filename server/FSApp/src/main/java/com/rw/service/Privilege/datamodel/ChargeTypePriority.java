package com.rw.service.Privilege.datamodel;

import java.util.Comparator;

import com.log.GameLog;
import com.playerdata.VipMgr;

public class ChargeTypePriority implements Comparator<String> {
	private static ChargeTypePriority instance;
	public static ChargeTypePriority getShareInstance(){
		if (instance == null){
			instance = new ChargeTypePriority();
		}
		return instance;
	}
	
	@Override
	public int compare(String leftChargeType, String rightChargeType) {
		boolean leftIsVip = leftChargeType.startsWith(VipMgr.vipPrefix);
		boolean rightIsVip = rightChargeType.startsWith(VipMgr.vipPrefix);
		boolean leftIsMonth = leftChargeType.startsWith(monthPrefix);
		boolean rightIsMonth = rightChargeType.startsWith(monthPrefix);

		if (leftIsVip && rightIsMonth){
			return -1;
		}
		
		if (leftIsMonth && rightIsVip){
			return 1;
		}
		
		if (leftIsVip && rightIsVip){
			int leftVip = extractVipLevel(leftChargeType);
			int rightVip = extractVipLevel(rightChargeType);
			return leftVip - rightVip;
		}
		
		if (leftIsMonth && rightIsMonth){
			int leftMonthLevel = extractMonthLevel(leftChargeType);
			int rightMonthLevel = extractMonthLevel(rightChargeType);
			return leftMonthLevel - rightMonthLevel;
		}
		
		//未知类型保持原序列！
		GameLog.error("特权", "计算充值类型优先级", "未知充值类型:"+leftChargeType+","+rightChargeType);
		return -1;
	}
	
	public static final String monthPrefix = "month";
	/**
	 * hard code 充值类型的优先级判断：月卡优先于vip
	 * @param leftChargeType
	 * @param rightChargeType
	 * @return
	 */
	public boolean gt(String leftChargeType, String rightChargeType) {
		return compare(leftChargeType,rightChargeType)>0;
	}

	private int extractMonthLevel(String chargeTy) {
		String monthVal = chargeTy.substring(chargeTy.indexOf(monthPrefix)+5);
		if ("normal".equals(monthVal)){
			return 0;
		}
		if ("vip".equals(monthVal)){
			return 1;
		}
		
		return -1;
	}

	private int extractVipLevel(String chargeTy) {
		return Integer.parseInt(chargeTy.substring(chargeTy.indexOf(VipMgr.vipPrefix)+VipMgr.vipPrefix.length()));
	}
}
