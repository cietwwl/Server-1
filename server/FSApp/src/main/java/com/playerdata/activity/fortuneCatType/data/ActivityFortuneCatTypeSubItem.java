package com.playerdata.activity.fortuneCatType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityFortuneCatTypeSubItem {
	
	private String cfgId;
	
	private int getGold;
	
	private String cost;
	
	private int num;
	
	private int vip ;
	
	
	
	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}



	public int getGetGold() {
		return getGold;
	}

	public void setGetGold(int getGold) {
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
