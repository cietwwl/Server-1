package com.playerdata.mgcsecret.data;

import java.util.ArrayList;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class UserMagicSecretData {

	@Id
	private String userId; // 用户ID

	@CombineSave
	private String secretArmy; // 战斗队伍情况

	@CombineSave
	int historyScore; // 历史积分

	@CombineSave
	int todayScore; // 当日积分

	@CombineSave
	ArrayList<Integer> gotScoreReward = new ArrayList<Integer>(); // 已经获取过的积分奖励

	@CombineSave
	int secretGold; // 秘境货币

	@CombineSave
	long recentScoreTime; // 最新获得积分时间

	@CombineSave
	private int maxStageID; // 关卡历史最高纪录

	@CombineSave
	private String currentDungeonID = null; // 正在打的副本
	
	@CombineSave
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
}
