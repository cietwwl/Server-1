package com.playerdata.charge.dao;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "charge_record")
public class ChargeRecord {
//	@Column(name = "id")
//	private long id;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "sdk_user_id")
	private String sdkUserId;
	@Column(name = "trade_no")
	@Id
	private String tradeNo;
	@Column(name = "money")
	private int money;
	@Column(name = "currency_type")
	private String currencyType;
	@Column(name = "channel_id")
	private String channelId;
	@Column(name = "item_id")
	private String itemId;
	@Column(name = "charge_time")
	private Timestamp _chargeTimeStamp;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getSdkUserId() {
		return sdkUserId;
	}
	
	public void setSdkUserId(String sdkUserId) {
		this.sdkUserId = sdkUserId;
	}
	
	public String getTradeNo() {
		return tradeNo;
	}
	
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setMoney(int money) {
		this.money = money;
	}
	
	public String getCurrencyType() {
		return currencyType;
	}
	
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public Timestamp getChargeTime() {
		return _chargeTimeStamp;
	}
	
	public void setChargeTime(long chargeTime) {
		this._chargeTimeStamp = new Timestamp(chargeTime);
	}
}
