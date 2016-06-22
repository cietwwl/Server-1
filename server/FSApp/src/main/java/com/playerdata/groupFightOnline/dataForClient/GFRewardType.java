package com.playerdata.groupFightOnline.dataForClient;

/**
 * 帮战结束时的奖励类型
 * @author aken
 */
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
