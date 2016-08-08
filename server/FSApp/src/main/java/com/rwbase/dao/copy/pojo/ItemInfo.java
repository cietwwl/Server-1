package com.rwbase.dao.copy.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.readonly.ItemInfoIF;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class ItemInfo implements ItemInfoIF
{
	private int itemID;
	private int itemNum;
	
	public ItemInfo() {}
	
	public ItemInfo(int pItemId, int pItemNum) {
		this.itemID = pItemId;
		this.itemNum = pItemNum;
	}
	
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

	@Override
	public String toString() {
		return "ItemInfo [itemID=" + itemID + ", itemNum=" + itemNum + "]";
	}
}
