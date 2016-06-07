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
