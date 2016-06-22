package com.rw.handler.magicSecret;

import java.util.ArrayList;





import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMagicSecretData implements  SynItem{

	private String userId; // 用户ID

	private String secretArmy; // 战斗队伍情况

	int historyScore; // 历史积分

	int todayScore; // 当日积分

	ArrayList<Integer> gotScoreReward = new ArrayList<Integer>(); // 已经获取过的积分奖励

	int secretGold; // 秘境货币

	long recentScoreTime; // 最新获得积分时间

	private int maxStageID; // 关卡历史最高纪录

	private String currentDungeonID = null; // 正在打的副本

	private long lastResetTime = 0l;
	
	public UserMagicSecretData(String userId) {
		this.userId = userId; 
		this.secretArmy = "";
	}
	
	public UserMagicSecretData() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSecretArmy() {
		return secretArmy;
	}

	public void setSecretArmy(String secretArmy) {
		this.secretArmy = secretArmy;
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

	public ArrayList<Integer> getGotScoreReward() {
		return gotScoreReward;
	}

	public void setGotScoreReward(ArrayList<Integer> gotScoreReward) {
		this.gotScoreReward = gotScoreReward;
	}

	public int getSecretGold() {
		return secretGold;
	}

	public void setSecretGold(int secretGold) {
		this.secretGold = secretGold;
	}

	public long getRecentScoreTime() {
		return recentScoreTime;
	}

	public void setRecentScoreTime(long recentScoreTime) {
		this.recentScoreTime = recentScoreTime;
	}
	
	public long getLastResetTime(){
		return lastResetTime;
	}

	public int getMaxStageID() {
		return maxStageID;
	}

	public void setMaxStageID(int maxStageID) {
		this.maxStageID = maxStageID;
	}

	public String getCurrentDungeonID() {
		return currentDungeonID;
	}

	public void setCurrentDungeonID(String currentDungeonID) {
		this.currentDungeonID = currentDungeonID;
	}
	
	public void saveDailyScoreData(){
		historyScore += todayScore;
		todayScore = 0;
		lastResetTime = System.currentTimeMillis();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
