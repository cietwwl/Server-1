package com.bm.targetSell.param;

/**
 * 5007 玩家获得物品后通知精准服参数
 * @author Alex
 * 2016年9月17日 下午5:40:00
 */
public class TargetSellGetItemParam extends TargetSellAbsArgs{

	/**玩家已经获得的物品组id*/
	private int itemGroupId;

	public int getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(int itemGroupId) {
		this.itemGroupId = itemGroupId;
	}
	
	
	
}
