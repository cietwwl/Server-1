package com.playerdata.activity.retrieve.data;

public class RewardBackSubItem {
	private int id;//功能id
	private int maxCount;//今天最大次数
	private int count;//今天已参与次数
	
	private String normalReward;//普通奖励
	private int normalType;
	private int normalCost;
	
	private String perfectReward;//完美奖励
	private int perfectType;
	private int perfectCost;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getNormalReward() {
		return normalReward;
	}
	public void setNormalReward(String normalReward) {
		this.normalReward = normalReward;
	}
	public int getNormalType() {
		return normalType;
	}
	public void setNormalType(int normalType) {
		this.normalType = normalType;
	}
	public int getNormalCost() {
		return normalCost;
	}
	public void setNormalCost(int normalCost) {
		this.normalCost = normalCost;
	}
	public String getPerfectReward() {
		return perfectReward;
	}
	public void setPerfectReward(String perfectReward) {
		this.perfectReward = perfectReward;
	}
	public int getPerfectType() {
		return perfectType;
	}
	public void setPerfectType(int perfectType) {
		this.perfectType = perfectType;
	}
	public int getPerfectCost() {
		return perfectCost;
	}
	public void setPerfectCost(int perfectCost) {
		this.perfectCost = perfectCost;
	}
	
	
	
	
}
