package com.playerdata.activity.dailyDiscountType.cfg;

public class ActivityDailyDiscountItemCfg {
	private String id;
	private String itemId;
	private int countLimit;
	private int priceAfterDiscount;
	
	
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
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public int getCountLimit() {
		return countLimit;
	}
	public void setCountLimit(int countLimit) {
		this.countLimit = countLimit;
	}
	
	
	
	
}
