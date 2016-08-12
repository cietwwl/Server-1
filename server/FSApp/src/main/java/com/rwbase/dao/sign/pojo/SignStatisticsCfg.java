package com.rwbase.dao.sign.pojo;

public class SignStatisticsCfg {
	private String ID;
	private String NextID;
	private int signNum;
	private String reward;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getNextID() {
		return NextID;
	}
	public void setNextID(String nextID) {
		NextID = nextID;
	}
	public int getSignNum() {
		return signNum;
	}
	public void setSignNum(int signNum) {
		this.signNum = signNum;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
}
