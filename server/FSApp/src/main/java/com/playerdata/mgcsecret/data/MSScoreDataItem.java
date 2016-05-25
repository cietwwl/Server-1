package com.playerdata.mgcsecret.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.mgcsecret.manager.MagicSecretMgr;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MSScoreDataItem {
	private String userId;
	private int historyScore; //历史积分
	private int todayScore;	//当日积分
	private long recentScoreTime; //最新获得积分时间

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getHistoryScore() {
		return historyScore;
	}

	public void setHistoryScore(int historyScore) {
		this.historyScore = historyScore;
	}

	public int getTodayScore() {
		return todayScore;
	}

	public void setTodayScore(int todayScore) {
		this.todayScore = todayScore;
	}

	public long getRecentScoreTime() {
		return recentScoreTime;
	}

	public void setRecentScoreTime(long recentScoreTime) {
		this.recentScoreTime = recentScoreTime;
	}
	
	public int getTotalScore(){
		return (int)(historyScore * MagicSecretMgr.SCORE_COEFFICIENT) + todayScore;
	}
}
