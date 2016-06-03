package com.bm.rank.magicsecret;

import com.playerdata.mgcsecret.manager.MagicSecretMgr;

public class MagicSecretComparable implements Comparable<MagicSecretComparable> {
	private long recentScoreTime; //最新获得积分时间
	private int totalScore; //总积分
	
	public MagicSecretComparable(){
		
	}
	
	public MagicSecretComparable(int historyScore, int todayScore, long recentScoreTime){
		this.recentScoreTime = recentScoreTime;
		this.totalScore = MagicSecretMgr.getTotalScore(historyScore, todayScore);
	}

	public long getRecentScoreTime() {
		return recentScoreTime;
	}
	
	public int getTotalScore(){
		return this.totalScore;
	}

	@Override
	public int compareTo(MagicSecretComparable o) {
		if(getTotalScore() < o.getTotalScore()){
			return -1;
		}
		if(getTotalScore() > o.getTotalScore()){
			return 1;
		}
		if(recentScoreTime > o.recentScoreTime){
			return -1;
		}
		if(recentScoreTime < o.recentScoreTime){
			return 1;
		}
		return 0;
	}
}
