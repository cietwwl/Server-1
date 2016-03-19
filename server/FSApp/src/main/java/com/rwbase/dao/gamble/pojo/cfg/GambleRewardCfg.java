package com.rwbase.dao.gamble.pojo.cfg;

import com.rwproto.GambleServiceProtos.EGambleType;

public class GambleRewardCfg {
	private int weightGroup;
	private String itemID;
	private int weight;
	private int max;
	private String belong;
	private int order;
	public int getWeightGroup() {
		return weightGroup;
	}
	public void setWeightGroup(int weightGroup) {
		this.weightGroup = weightGroup;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	/**这件物品会不会掉落这个垂钓类型的物品*/
	public boolean hasGambleType(EGambleType type){
		int num = getBelong().indexOf(type.getNumber() + "");
		return num != -1;
	}
}
