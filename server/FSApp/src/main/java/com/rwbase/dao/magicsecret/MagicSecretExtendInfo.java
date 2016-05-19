package com.rwbase.dao.magicsecret;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MagicSecretExtendInfo {
	private String userId; // 用户ID
	
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
	
	
}
