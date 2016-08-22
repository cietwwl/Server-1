package com.playerdata.activity.fortuneCatType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityFortuneCatTypeSubItem {
	
	private String cfgId;
	
	private String getGold;
	
	private String cost;
	
	private int num;
	
	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getGetGold() {
		return getGold;
	}

	public void setGetGold(String getGold) {
		this.getGold = getGold;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	

}
