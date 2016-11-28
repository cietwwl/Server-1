package com.rw.service.gamble.datamodel;

public class GambleAdwardItem {
	private String itemId;
	private int itemNum;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	@Override
	public String toString() {
		return "GambleAdwardItem [itemId=" + itemId + ", itemNum=" + itemNum + "]";
	}

	
}
