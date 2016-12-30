package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;

public class ActivityDailyDiscountTypeSubCfg implements ActivitySubCfgIF{
	private int id;
	private int parentId;
	private int afterSomeDays;
	private String itemIdList;
	private List<Integer> itemList = new ArrayList<Integer>();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getParentId() {
		return parentId;
	}
	
	public void setParentId(int parentId) {
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

	@Override
	public int getDay() {
		return afterSomeDays;
	}

	@Override
	public int getType() {
		return parentId;
	}

	@Override
	public void setCfgReward(String reward) {
		// TODO Auto-generated method stub
		
	}	
}
