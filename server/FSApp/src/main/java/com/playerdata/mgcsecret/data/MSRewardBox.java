package com.playerdata.mgcsecret.data;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class MSRewardBox {
	private ItemInfo boxCost;
	private String dropStr;
	
	public ItemInfo getBoxCost() {
		return boxCost;
	}
	
	public void setBoxCost(ItemInfo boxCost) {
		this.boxCost = boxCost;
	}
	
	public String getDropStr() {
		return dropStr;
	}
	
	public void setDropStr(String dropStr) {
		this.dropStr = dropStr;
	}
}
