package com.rwbase.dao.magicsecret.cfg;


public class MagicScoreRankCfg
{
	private int key; 
	private int stageId; 
	private int rankEnd; 
	private String reward; 

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

	public void setKey(int key) {
		this.key = key;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public void setRankEnd(int rankEnd) {
		this.rankEnd = rankEnd;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}
	
}