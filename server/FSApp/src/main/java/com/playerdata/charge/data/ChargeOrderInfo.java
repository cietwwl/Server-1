package com.playerdata.charge.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class ChargeOrderInfo {
	
	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
