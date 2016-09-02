package com.playerdata.activity.limitHeroType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityLimitHeroTypeSubItem {
	private String cfgId;
	
	private int integral;
	
	private boolean isTanken;
	
	private String rewards;

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public boolean isTanken() {
		return isTanken;
	}

	public void setTanken(boolean isTanken) {
		this.isTanken = isTanken;
	}

	public String getRewards() {
		return rewards;
	}

	public void setRewards(String rewards) {
		this.rewards = rewards;
	}
	
	
	
}
