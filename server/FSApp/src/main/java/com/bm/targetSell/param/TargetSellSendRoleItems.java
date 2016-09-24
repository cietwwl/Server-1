package com.bm.targetSell.param;

import java.util.List;

import com.bm.targetSell.TargetSellManager;
import com.rwbase.dao.targetSell.BenefitItems;

/**
 * 5004 精准服推送玩家物品到游戏服（更新玩家物品）
 * @author Alex
 * 2016年9月17日 下午6:18:57
 */
public class TargetSellSendRoleItems extends TargetSellAbsArgs {
	
	private String actionName;
	
	private List<BenefitItems> items;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public List<BenefitItems> getItems() {
		return items;
	}

	public void setItems(List<BenefitItems> items) {
		this.items = items;
	}

	@Override
	public void excuteMsg(int msgType) {
		TargetSellManager.getInstance().updateRoleItems(this);
	}

	
	
}
