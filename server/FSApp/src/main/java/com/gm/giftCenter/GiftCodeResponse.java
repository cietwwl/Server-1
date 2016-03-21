package com.gm.giftCenter;


public class GiftCodeResponse {

	private int gift_id;
	private int type;
	private long iSequenceNum;
	private String title;
	private GiftItem itemData;	
	
	public int getGift_id() {
		return gift_id;
	}
	public void setGift_id(int gift_id) {
		this.gift_id = gift_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getiSequenceNum() {
		return iSequenceNum;
	}
	public void setiSequenceNum(long iSequenceNum) {
		this.iSequenceNum = iSequenceNum;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public GiftItem getItemData() {
		return itemData;
	}
	public void setItemData(GiftItem itemData) {
		this.itemData = itemData;
	}

	
	
	
	
}
