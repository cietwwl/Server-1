package com.playerdata.mgcsecret.cfg;
import com.common.BaseConfig;

public class DungeonScoreCfg extends BaseConfig {
	private int key; //关键字段
	private int score; //积分
	private String reward; //奖励

	public int getKey() {
		return key;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getReward() {
		return reward;
	}

}
