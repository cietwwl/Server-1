package com.rwbase.dao.guildSecretArea;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.guildSecretArea.projo.SecretAttrackInfo;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea")
public class TableSecretArea {
	@Id
	private String secretId;
	private String playerId;
	
	private long fightTime;//开始战斗时间
	private int fightValue;//战斗力
	private Map<String,Long> begianTimeMap;//开始时间<playerid,时间>
	private long endTime;//结束时间
	
	private ESecretType secretType;
	private Map<String,List<String>> playerHeroIdList;//防守玩家
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public long getFightTime() {
		return fightTime;
	}
	public void setFightTime(long fightTime) {
		this.fightTime = fightTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}


	public Map<String,Long> getBegianTimeMap() {
		return begianTimeMap;
	}
	public void setBegianTimeMap(Map<String,Long> begianTimeMap) {
		this.begianTimeMap = begianTimeMap;
	}
	public ESecretType getSecretType() {
		return secretType;
	}
	public void setSecretType(ESecretType secretType) {
		this.secretType = secretType;
	}
	public Map<String,List<String>> getPlayerHeroIdList() {
		return playerHeroIdList;
	}
	public void setPlayerHeroIdList(Map<String,List<String>> playerHeroIdList) {
		this.playerHeroIdList = playerHeroIdList;
	}
	public int getFightValue() {
		return fightValue;
	}
	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}
}
