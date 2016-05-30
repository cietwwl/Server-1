package com.rw.service.Privilege.datamodel;

import java.util.Comparator;

import com.log.GameLog;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;

public class ChargeTypePriority implements Comparator<String> {
	public static final String monthPrefix = "month";
	public static final String vipPrefix = "vip";
	
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
		
		int vip = VipPrivilegeHelper.getShareInstance().extractVipLevel(chargeType);
		if (vip >= 0){
			return vipPrefix + (vip > 0 ? vip - 1 : 0);
		}
		return MonthCardPrivilegeMgr.getShareInstance().guessPreviousChargeLevel(chargeType);
	}
	
	public int getBestMatchCharge(String[] sources,String chargeType) {
		if (chargeType == null || sources == null) {
			return -1;
		}
		
		VipPrivilegeHelper vipHelper = VipPrivilegeHelper.getShareInstance();
		int vip = vipHelper.extractVipLevel(chargeType);
		if (vip >= 0){
			return vipHelper.getBestMatchCharge(sources, vip);
		}
		
		if (!chargeType.startsWith(monthPrefix)){
			return -1;
		}
		
		//搜索可以充值的最大优先级的月卡
		MonthCardPrivilegeMgr monthHelper = MonthCardPrivilegeMgr.getShareInstance();
		int monthLevel = monthHelper.extractMonthLevel(chargeType);
		int bestMatchIndex = monthHelper.getBestMatchCharge(sources, monthLevel);
		
		if (bestMatchIndex == -1){
			//还找不到就使用最后保底方案，使用Vip0!
			for(int i = 0;i<sources.length;i++){
				if (sources[i].equals(vipPrefix+"0")){
					bestMatchIndex = i;
				}
			}
		}
		
		return bestMatchIndex;
	}
	
	@Override
	public int compare(String leftChargeType, String rightChargeType) {
		boolean leftIsVip = leftChargeType.startsWith(vipPrefix);
		boolean rightIsVip = rightChargeType.startsWith(vipPrefix);
		boolean leftIsMonth = leftChargeType.startsWith(monthPrefix);
		boolean rightIsMonth = rightChargeType.startsWith(monthPrefix);

		if (leftIsVip && rightIsMonth){
			return -1;
		}
		
		if (leftIsMonth && rightIsVip){
			return 1;
		}
		
		if (leftIsVip && rightIsVip){
			VipPrivilegeHelper vipHelper = VipPrivilegeHelper.getShareInstance();
			int leftVip = vipHelper.extractVipLevel(leftChargeType);
			int rightVip = vipHelper.extractVipLevel(rightChargeType);
			return leftVip - rightVip;
		}
		
		if (leftIsMonth && rightIsMonth){
			MonthCardPrivilegeMgr monthHelper = MonthCardPrivilegeMgr.getShareInstance();
			int leftMonthLevel = monthHelper.extractMonthLevel(leftChargeType);
			int rightMonthLevel = monthHelper.extractMonthLevel(rightChargeType);
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
}
