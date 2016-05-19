package com.bm.rank.magicsecret;

public class MagicSecretComparable implements Comparable<MagicSecretComparable> {
	private final int historyScore; //历史积分
	private final int todayScore;	//当日积分
	private final long recentScoreTime; //最新获得积分时间
	
	public MagicSecretComparable(int historyScore, int todayScore, long recentScoreTime){
		this.historyScore = historyScore;
		this.todayScore = todayScore;
		this.recentScoreTime = recentScoreTime;
	}

	@Override
	public int compareTo(MagicSecretComparable o) {
		if(historyScore + todayScore < o.historyScore + o.todayScore){
			return -1;
		}
		if(historyScore + todayScore > o.historyScore + o.todayScore){
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
