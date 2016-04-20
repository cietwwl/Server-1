package com.playerdata.charge.cfg;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeCfg {
	
	
	private int firstChargeAwardTimes;
	
	private int firstChargeAwardMax;

	private List<ChargeItem> chargetTypeList = new ArrayList<ChargeItem>();

	private String chargeItems;

	public List<ChargeItem> getChargetTypeList() {
		return chargetTypeList;
	}

	public void setChargetTypeList(List<ChargeItem> chargetTypeList) {
		this.chargetTypeList = chargetTypeList;
	}


	public String getChargeItems() {
		return chargeItems;
	}

	public void setChargeItems(String chargeItems) {
		this.chargeItems = chargeItems;
	}

	public int getFirstChargeAwardTimes() {
		return firstChargeAwardTimes;
	}

	public void setFirstChargeAwardTimes(int firstChargeAwardTimes) {
		this.firstChargeAwardTimes = firstChargeAwardTimes;
	}

	public int getFirstChargeAwardMax() {
		return firstChargeAwardMax;
	}

	public void setFirstChargeAwardMax(int firstChargeAwardMax) {
		this.firstChargeAwardMax = firstChargeAwardMax;
	}
	
	
	
	
}
