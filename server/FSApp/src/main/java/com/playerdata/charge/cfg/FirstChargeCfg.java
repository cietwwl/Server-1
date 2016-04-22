package com.playerdata.charge.cfg;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstChargeCfg {
		
	private int awardTimes;
	
	private int awardMax;
	
	private String reward;
	
	private Map<String, Integer> giftMap = new HashMap<String, Integer>();
	
	public Map<String, Integer> getGiftMap() {
		return giftMap;
	}

	public void setGiftMap(Map<String, Integer> giftMap) {
		this.giftMap = giftMap;
	}

	
	public int getAwardTimes() {
		return awardTimes;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
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
