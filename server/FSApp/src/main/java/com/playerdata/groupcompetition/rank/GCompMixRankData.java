package com.playerdata.groupcompetition.rank;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompMixRankData {
	
	private String userId;	
	
	private String userName;
	
	private int score;
	
	private int kill;
	
	private int continueWin;
	
	public GCompMixRankData(String userId, String userName, int score, int kill, int continueWin){
		this.userId = userId;
		this.userName = userName;
		this.score = score;
		this.kill = kill;
		this.continueWin = continueWin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getKill() {
		return kill;
	}

	public void setKill(int kill) {
		this.kill = kill;
	}

	public int getContinueWin() {
		return continueWin;
	}

	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}
}
