package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.ArrayList;
import java.util.List;

public class ActivityDailyDiscountTypeSubCfg {
	private int id;
	private String parentId;
	private int afterSomeDays;
	private String itemIdList;
	private List<Integer> itemList = new ArrayList<Integer>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getAfterSomeDays() {
		return afterSomeDays;
	}
	public void setAfterSomeDays(int afterSomeDays) {
		this.afterSomeDays = afterSomeDays;
	}
	public String getItemIdList() {
		return itemIdList;
	}
	public void setItemIdList(String itemIdList) {
		this.itemIdList = itemIdList;
	}
	public List<Integer> getItemList() {
		return itemList;
	}
	public void setItemList(List<Integer> itemList) {
		this.itemList = itemList;
	}	
}
