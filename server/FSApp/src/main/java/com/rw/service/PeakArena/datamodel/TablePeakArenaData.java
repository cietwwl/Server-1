package com.rw.service.PeakArena.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "peak_arena_data")
public class TablePeakArenaData {
	@Id
	private String userId; // 用户ID
	private int maxPlace;
	private int winCount;//历史胜利场次
	private boolean hasRanking;//未入榜的标志
	private int challengeCount;//挑战次数
	private int resetCount;//重置次数
	private int buyCount;//额外购买的挑战次数
	private int career;
	private String name;
	private String headImage;
	private String templeteId;
	private int expectCurrency; // 预计能获得的货币
	private int level;
	private int fighting;//战力
	private volatile long fightStartTime;//开战时间,0表示没有开战
	private TeamData[] teams = new TeamData[3];
	
	private List<PeakRecordInfo> recordList; //TODO change to simple array
	private long lastGainCurrencyTime; // 上次获取货币的时间
	private int lastScore; // 最后一次分数
	private String lastFightEnemy;

	public TablePeakArenaData() {
		this.recordList = new ArrayList<PeakRecordInfo>();
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

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	// public int getMagicId() {
	// return magicId;
	// }
	// public void setMagicId(int magicId) {
	// this.magicId = magicId;
	// }
	// public int getMagicLevel() {
	// return magicLevel;
	// }
	// public void setMagicLevel(int magicLevel) {
	// this.magicLevel = magicLevel;
	// }
	public String getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(String templeteId) {
		this.templeteId = templeteId;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	public List<PeakRecordInfo> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<PeakRecordInfo> recordList) {
		if (recordList != null) {
			this.recordList = recordList;
		}
	}

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

	public int getLastScore() {
		return lastScore;
	}

	public void setLastScore(int lastScore) {
		this.lastScore = lastScore;
	}

	public void setLastFightEnemy(String enemyId) {
		lastFightEnemy = enemyId;
	}

	public String getLastFightEnemy() {
		return lastFightEnemy;
	}
}
