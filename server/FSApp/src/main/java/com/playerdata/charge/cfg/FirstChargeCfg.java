package com.playerdata.charge.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstChargeCfg {
	
	
	private int firstChargeAwardTimes;
	
	private int firstChargeAwardMax;

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
