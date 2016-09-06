package com.playerdata.groupcompetition.holder.data;

public class GCompMember implements Comparable<GCompMember>{

	private String userId; // 玩家id
	private int lv; // 玩家等级
	private int score; // 积分
	private int totalWinTimes; // 胜利的次数
	private int continueWins; // 连胜次数
	private int maxContinueWins; // 最大连胜次数
	
	public GCompMember(String userId, int lv) {
		this.userId = userId;
		this.lv = lv;
	}
	
	public int getLv() {
		return lv;
	}

	public String getUserId() {
		return userId;
	}

	public int getScore() {
		return score;
	}
	
	public void updateScore(int offset) {
		this.score += offset;
	}

	public int getContinueWins() {
		return continueWins;
	}
	
	public int getTotalWinTimes() {
		return totalWinTimes;
	}
	
	public void incWinTimes() {
		this.totalWinTimes++;
		this.continueWins++;
		if (this.maxContinueWins < this.continueWins) {
			this.maxContinueWins = this.continueWins;
		}
	}
	
	public void resetContinueWins() {
		this.continueWins = 0;
	}

	public int getMaxContinueWins() {
		return maxContinueWins;
	}

	@Override
	public int compareTo(GCompMember o) {
		return this.lv > o.lv ? -1 : 1;
	}
}
