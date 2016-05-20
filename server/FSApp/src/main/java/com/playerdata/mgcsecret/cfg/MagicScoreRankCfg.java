package com.playerdata.mgcsecret.cfg;
import com.common.BaseConfig;

public class MagicScoreRankCfg extends BaseConfig {
	private int key; //关键字段
	private int stageId; //阶段ID
	private int rankEnd; //排名start
	private String reward; //奖励

	public int getKey() {
		return key;
	}
	
	public int getStageId() {
		return stageId;
	}
	
	public int getRankEnd() {
		return rankEnd;
	}
	
	public String getReward() {
		return reward;
	}

}
