package com.rw.handler.majordata;

import com.rw.dataSyn.SynItem;

public class MajorData implements  SynItem  {
	private String id;
	private String ownerId;

	private long coin;// 铜钱

	private int gold;// 赠送金钱,展示用
	private int giftGold;// 赠送金钱
	private int chargeGold;// 充值金钱
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public long getCoin() {
		return coin;
	}
	public void setCoin(long coin) {
		this.coin = coin;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getGiftGold() {
		return giftGold;
	}
	public void setGiftGold(int giftGold) {
		this.giftGold = giftGold;
	}
	public int getChargeGold() {
		return chargeGold;
	}
	public void setChargeGold(int chargeGold) {
		this.chargeGold = chargeGold;
	}
}
