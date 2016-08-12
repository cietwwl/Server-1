package com.playerdata.activity.dailyDiscountType.cfg;

public class ActivityDailyDiscountItemCfg {
	private String id;
	private String itemIdAndNum;
	private int countLimit;
	private int priceAfterDiscount;
	private int itemId;
	private int itemNum;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getPriceAfterDiscount() {
		return priceAfterDiscount;
	}
	public void setPriceAfterDiscount(int priceAfterDiscount) {
		this.priceAfterDiscount = priceAfterDiscount;
	}

	public int getCountLimit() {
		return countLimit;
	}
	public void setCountLimit(int countLimit) {
		this.countLimit = countLimit;
	}
	public String getItemIdAndNum() {
		return itemIdAndNum;
	}
	public void setItemIdAndNum(String itemIdAndNum) {
		this.itemIdAndNum = itemIdAndNum;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	
	
	
	
}
