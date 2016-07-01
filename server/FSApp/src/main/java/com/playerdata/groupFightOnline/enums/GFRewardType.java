package com.playerdata.groupFightOnline.enums;

/**
 * 帮战结束时的奖励类型
 * @author aken
 */
public enum GFRewardType {
	/**
	 * 没有奖励
	 */
	NoReward(0),
	/**
	 * 杀敌数排行奖励
	 */
	KillRankReward(1),
	/**
	 * 伤害排行奖励
	 */
	HurtRankReward(2),
	/**
	 * 帮战胜利奖励
	 */
	GFightSuccessReward(3),
	/**
	 * 帮战失败奖励
	 */
	GFihgtFailReward(4);
	
	private int value;
	GFRewardType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public boolean equals(int value){
		return this.value == value;
	}
	
	public boolean equals(GFRewardType state){
		return this.value == state.value;
	}
	
	public GFRewardType getState(int value){
		switch (value) {
		case 1:
			return KillRankReward;
		case 2:
			return HurtRankReward;
		case 3:
			return GFightSuccessReward;
		case 4:
			return GFihgtFailReward;
		default:
			return NoReward;
		}
	}
}
