package com.playerdata.groupFightOnline.dataExtend;

public enum RewardType {
	KillRankReward(1);
	
	private int value;
	RewardType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
