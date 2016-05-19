package com.playerdata.mgcsecret.cfg;


public class BuffBonusCfg
{
	private String key; 
	private int layerID; 
	private int rate;
  	private int buffId;
  	private int cost;

  	public void setKey(String key) {
		this.key = key;
	}

	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public void setBuffId(int buffId) {
		this.buffId = buffId;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getKey() {
  		return key;
  	}
  	
  	public int getLayerID() {
  		return layerID;
  	}
  	
  	public int getRate() {
  		return rate;
  	}
  	
  	public int getBuffId() {
  		return buffId;
  	}
  	
  	public int getCost() {
  		return cost;
  	}
  	
}