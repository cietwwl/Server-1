package com.playerdata.charge.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class ChargeParam {
	
	private String productId;
	
	private String chargeEntrance;      //充值入口

	private String friendId;
	
	private ChargeOrderInfo orderInfo;

	public String getProductId() {
		return productId;
	}

	public String getChargeEntrance() {
		return chargeEntrance;
	}

	public String getFriendId() {
		return friendId;
	}

	public ChargeOrderInfo getOrderInfo() {
		return orderInfo;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setChargeEntrance(String chargeEntrance) {
		this.chargeEntrance = chargeEntrance;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public void setOrderInfo(ChargeOrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}
}
