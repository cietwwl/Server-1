package com.rwbase.dao.worship.pojo;

public class CfgWorshipRankdomScheme {
	private String probability;

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}
	
	public String[] getProbabilityList(){
		return this.probability.split(",");
	}
}
