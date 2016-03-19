package com.rwbase.dao.sign.pojo;

public class SignCfg 
{
	private String itemID;	//物品ID...
	private int itemNum;	//物品数量...
	private int vipLimit;	//VIP限制...
	
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	public int getVipLimit() {
		return vipLimit;
	}
	public void setVipLimit(int vipLimit) {
		this.vipLimit = vipLimit;
	}
	
}

