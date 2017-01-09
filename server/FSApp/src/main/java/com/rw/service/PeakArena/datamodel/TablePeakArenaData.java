package com.rw.service.PeakArena.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Table(name = "peak_arena_data")
public class TablePeakArenaData {
	@Id
	private String userId; // 用户ID
	private int maxPlace; //历史最高排名
	private int winCount;//历史胜利场次
	private boolean hasRanking;//未入榜的标志
	
	private int challengeCount;//挑战次数 每天重置
	private int resetCount;//重置次数 每天重置
	private int buyCount;//额外购买的挑战次数 每天重置
	
	private int expectCurrency; // 预计能获得的货币
	private volatile long fightStartTime;//开战时间,0表示没有开战
	private TeamData[] teams = new TeamData[3];
	
//	private List<PeakRecordInfo> recordList;
	private long lastGainCurrencyTime; // 上次获取货币的时间
	private String lastFightEnemy;
	private int score; // 积分
	private List<Integer> rewardList = new ArrayList<Integer>();
	private AtomicInteger recordIdGenerator = new AtomicInteger();
	private int lastResetDayOfYear;

	public TablePeakArenaData() {
//		this.recordList = new ArrayList<PeakRecordInfo>();
	}
	
	public TeamData[] getTeams() {
		return teams;
	}

	public void setTeams(TeamData[] teams) {
		this.teams = teams;
	}

	public static TeamData search(int teamId, TeamData[] teams) {
		if (teams == null) return null;
		for(int i = 0; i<teams.length;i++){
			TeamData team = teams[i];
			if (team != null && team.getTeamId() == teamId){
				return team;
			}
		}
		return null;
	}
	
	public TeamData search(int teamId){
		return search(teamId,this.teams);
	}

	public TeamData getTeam(int index) {
		if (0<= index && index < teams.length){
			return teams[index];
		}
		return null;
	}
	
	public int getTeamCount(){
		return teams.length;
	}
	
	public boolean setTeam(TeamData team,int index) {
		if (0<= index && index < teams.length){
			teams[index] = team;
			return true;
		}else{
			//GameLog.error("巅峰竞技场", "更新队伍参数有错", "index out of bound:"+index);
			return false;
		}
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public long getFightStartTime() {
		return fightStartTime;
	}

	public void setFightStartTime(long fightStartTime) {
		this.fightStartTime = fightStartTime;
	}

	public int getChallengeCount() {
		return challengeCount;
	}

	public void setChallengeCount(int challengeCount) {
		this.challengeCount = challengeCount;
	}

	public int getResetCount() {
		return resetCount;
	}

	public void setResetCount(int resetCount) {
		this.resetCount = resetCount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMaxPlace() {
		return maxPlace;
	}

	public void setMaxPlace(int maxPlace) {
		this.maxPlace = maxPlace;
		hasRanking = true;
	}
	
	public void setMaxPlace(int maxPlace,boolean hasRanking) {
		this.maxPlace = maxPlace;
		this.hasRanking = hasRanking;
	}
	
	public boolean getHasRanking(){
		return hasRanking;
	}

//	public List<PeakRecordInfo> getRecordList() {
//		return recordList;
//	}
//
//	public void setRecordList(List<PeakRecordInfo> recordList) {
//		if (recordList != null) {
//			this.recordList = recordList;
//		}
//	}

	public int getExpectCurrency() {
		return expectCurrency;
	}

	public void setExpectCurrency(int expectCurrency) {
		this.expectCurrency = expectCurrency;
	}

	public long getLastGainCurrencyTime() {
		return lastGainCurrencyTime;
	}

	public void setLastGainCurrencyTime(long lastGainCurrencyTime) {
		this.lastGainCurrencyTime = lastGainCurrencyTime;
	}

	public void setLastFightEnemy(String enemyId) {
		lastFightEnemy = enemyId;
	}

	public String getLastFightEnemy() {
		return lastFightEnemy;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public List<Integer> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<Integer> rewardList) {
		this.rewardList = rewardList;
	}
	
	public void resetRewardList() {
		this.rewardList.clear();
	}
	
	@JsonIgnore
	public int getCurrentRecordId() {
		return this.recordIdGenerator.get();
	}
	
	@JsonIgnore
	public int getNextId() {
		this.recordIdGenerator.compareAndSet(Short.MAX_VALUE, 0);
		return this.recordIdGenerator.incrementAndGet();
	}

	public int getLastResetDayOfYear() {
		return lastResetDayOfYear;
	}

	public void setLastResetDayOfYear(int lastResetDayOfYear) {
		this.lastResetDayOfYear = lastResetDayOfYear;
	}
}
