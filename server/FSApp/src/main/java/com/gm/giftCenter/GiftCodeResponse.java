package com.gm.giftCenter;

import java.util.List;

public class GiftCodeResponse {

	private int gift_id;
	private int type;
	private long iSequenceNum;
	private String title;
	private String content;
	private List<GiftItem> itemData;

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

	public void setItemData(List<GiftItem> itemData) {
		this.itemData = itemData;
	}

	public List<GiftItem> getItemData() {
		return itemData;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}