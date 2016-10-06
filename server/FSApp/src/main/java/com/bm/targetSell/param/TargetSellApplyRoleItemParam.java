package com.bm.targetSell.param;


/**
 * 5006 向精准营销服请求玩家可获取物品
 * @author Alex
 * 2016年9月17日 下午5:34:33
 */
public class TargetSellApplyRoleItemParam extends TargetSellAbsArgs{

	//触发动作，默认为all，请求所有符合条件道具
	private String actionName = "all";

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	
	
}
