package com.bm.targetSell.param;


/**
 * 5006 向精准营销服请求玩家可获取物品
 * @author Alex
 * 2016年9月17日 下午5:34:33
 */
public class TargetSellApplyRoleItemParam extends TargetSellAbsArgs{

	//触发动作，充值后的请求动作为recharge,无充值则为reward
	private String actionName;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	
	
}
