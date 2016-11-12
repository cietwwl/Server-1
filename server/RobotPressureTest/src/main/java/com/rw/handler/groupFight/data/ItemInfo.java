package com.rw.handler.groupFight.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemInfo
{
	private int itemID;
	private int itemNum;
	
	public int getItemID() {
		return itemID;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
}
