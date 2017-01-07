package com.playerdata.activity.fortuneCatType.cfg;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;


public class ActivityFortuneCatTypeSubCfg implements ActivitySubCfgIF{
	
	private int id;
	
	private int parentid;
	
	private int cost;
	
	private int min;
	
	private int max;
	
	private int vip;
	
	private int num;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public int getVip() {
		return vip;
	}
	
	public void setVip(int vip) {
		this.vip = vip;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getParentid() {
		return parentid;
	}
	
	@Override
	public int getDay() {
		return 1;
	}

	@Override
	public int getType() {
		return parentid;
	}

	@Override
	public void setCfgReward(String reward) {
		
	}
}
