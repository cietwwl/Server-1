package com.playerdata.mgcsecret.cfg;

public class BuffBonusCfg {
	private String key; //关键字段
	private int layerID; //方案id
	private int rate; //权重
	private int buffId; //属性类型
	private int cost; //价格(消耗星星)
	
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
