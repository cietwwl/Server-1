package com.playerdata.charge.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VipGiftCfg {
		
	//id就是vip等级
	private String id;
	
	//对应vip可购买的商品id
	private String chargeCfgId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChargeCfgId() {
		return chargeCfgId;
	}

	public void setChargeCfgId(String chargeCfgId) {
		this.chargeCfgId = chargeCfgId;
	}


	
	
	
	
}
