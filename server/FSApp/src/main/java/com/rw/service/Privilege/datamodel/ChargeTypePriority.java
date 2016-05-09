package com.rw.service.Privilege.datamodel;

import java.util.Comparator;

import com.log.GameLog;
import com.playerdata.VipMgr;

public class ChargeTypePriority implements Comparator<String> {
	public static final String monthPrefix = "month";
	private static String[] monthLevelStr = { "normal", "vip" };
	private static ChargeTypePriority instance;

	public static ChargeTypePriority getShareInstance(){
		if (instance == null){
			instance = new ChargeTypePriority();
		}
		return instance;
	}
	
	public String guessPreviousChargeLevel(String chargeType){
		if (chargeType == null) {
			return null;
		}
		
		if (chargeType.startsWith(VipMgr.vipPrefix)){
			int vip = extractVipLevel(chargeType);
			return VipMgr.vipPrefix + (vip > 0 ? vip - 1 : 0);
		}
		if (chargeType.startsWith(monthPrefix)){
			int monthLevel = extractMonthLevel(chargeType);
			//普通月卡的前一档定义为VIP0
			return (monthLevel > 0 ? monthPrefix + monthLevelStr[monthLevel] : VipMgr.vipPrefix + "0");
		}
		//无法估计前一档充值等级！
		return chargeType;
	}
	
	public int getBestMatchCharge(String[] sources,String chargeType) {
		if (chargeType == null || sources == null) {
			return -1;
		}
		
		if (chargeType.startsWith(VipMgr.vipPrefix)){
			int vip = extractVipLevel(chargeType);
			return VipPrivilegeHelper.getShareInstance().getBestMatchCharge(sources, vip);
		}
		
		if (!chargeType.startsWith(monthPrefix)){
			return -1;
		}
		
		//搜索可以充值的最大优先级的月卡
		int monthLevel = extractMonthLevel(chargeType);
		int maxMonthLevel = -1;
		int bestMatchIndex = -1; 
		for(int i = 0;i<sources.length;i++){
			if (sources[i].startsWith(monthPrefix)){
				int srcMonthLevel = extractMonthLevel(sources[i]);
				if (srcMonthLevel > maxMonthLevel && maxMonthLevel<=monthLevel){
					maxMonthLevel = srcMonthLevel;
					bestMatchIndex = i;
				}
			}
		}
		
		if (bestMatchIndex == -1){
			//还找不到就使用最后保底方案，使用Vip0!
			for(int i = 0;i<sources.length;i++){
				if (sources[i].equals(VipMgr.vipPrefix+"0")){
					bestMatchIndex = i;
				}
			}
		}
		
		return bestMatchIndex;
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
		String monthVal = chargeTy.substring(chargeTy.indexOf(monthPrefix)+monthPrefix.length());
		for (int i= 0;i<monthLevelStr.length;i++){
			if (monthLevelStr[i].equals(monthVal)){
				return i;
			}
		}
		return -1;
	}

	private int extractVipLevel(String chargeTy) {
		return Integer.parseInt(chargeTy.substring(chargeTy.indexOf(VipMgr.vipPrefix)+VipMgr.vipPrefix.length()));
	}
}
