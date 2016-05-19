package com.playerdata.mgcsecret.cfg;


public class DungeonScoreCfg
{
	private int key;
	private int score;
	private String reward;

	public int getKey() {
		return key;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getReward() {
		return reward;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

}