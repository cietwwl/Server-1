package com.playerdata.charge.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class ChargeParam {

	private String productId;

	private String chargeEntrance; // 充值入口

	private String friendId;

	private ChargeOrderInfo orderInfo;

	private String imei = "TestIMEI4WindowsUser";// 用户的IMEI码

	private String sysVer = "win10.0";// 用户的系统版本号

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

	public String getImei() {
		return imei;
	}

	public String getSysVer() {
		return sysVer;
	}
}