package com.playerdata.charge.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class ChargeOrderInfo {
	
	public static final String TEST_KEY = "182FA1C42A2468F8488E6DCF75A81B81";
	
	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
