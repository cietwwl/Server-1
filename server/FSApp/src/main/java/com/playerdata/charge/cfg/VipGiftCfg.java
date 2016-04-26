package com.playerdata.charge.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VipGiftCfg {
		
	//id就是vip等级
	private String vipLv;
	
	//对应vip可购买的商品id
	private String gift;
	
	//实际购买价格
	private String curCost;

	public String getVipLv() {
		return vipLv;
	}

	public void setVipLv(String vipLv) {
		this.vipLv = vipLv;
	}

	public String getGift() {
		return gift;
	}

	public void setGift(String gift) {
		this.gift = gift;
	}

	
	




	public String getCurCost() {
		return curCost;
	}

	public void setCurCost(String curCost) {
		this.curCost = curCost;
	}
	
	
	
}
