package com.playerdata.groupFightOnline.dataExtend;

public enum GFRewardType {
	KillRankReward(1);
	
	private int value;
	GFRewardType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
