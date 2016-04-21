package com.playerdata.charge.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstChargeCfg {
		
	private int awardTimes;
	
	private int awardMax;

	public int getAwardTimes() {
		return awardTimes;
	}

	public void setAwardTimes(int awardTimes) {
		this.awardTimes = awardTimes;
	}

	public int getAwardMax() {
		return awardMax;
	}

	public void setAwardMax(int awardMax) {
		this.awardMax = awardMax;
	}

	
	
	
}
