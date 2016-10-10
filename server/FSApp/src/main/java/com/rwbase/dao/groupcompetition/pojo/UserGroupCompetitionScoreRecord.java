package com.rwbase.dao.groupcompetition.pojo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.playerdata.groupcompetition.util.GCEventsType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupCompetitionScoreRecord {

	@JsonProperty("1")
	private GCEventsType eventsType;
	@JsonProperty("2")
	private String groupId;
	@JsonProperty("3")
	private String groupName;
	@JsonProperty("4")
	private int score; // 积分
	@JsonProperty("5")
	private int maxContinueWins; // 最大连胜
	@JsonProperty("6")
	private int totalWinTimes; // 击杀次数
	@JsonIgnore
	private int updateTimes;
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
		updateTimes++;
	}
	
	public int getMaxContinueWins() {
		return maxContinueWins;
	}
	
	public void setMaxContinueWins(int maxContinueWins) {
		this.maxContinueWins = maxContinueWins;
		updateTimes++;
	}
	
	public int getTotalWinTimes() {
		return totalWinTimes;
	}
	
	public void setTotalWinTimes(int totalWinTimes) {
		this.totalWinTimes = totalWinTimes;
		updateTimes++;
	}

	public GCEventsType getEventsType() {
		return eventsType;
	}

	public void setEventsType(GCEventsType eventsType) {
		this.eventsType = eventsType;
	}

	public int getUpdateTimes() {
		return updateTimes;
	}

	public void setUpdateTimes(int updateTimes) {
		this.updateTimes = updateTimes;
	}
}
