package com.rwbase.dao.ranking;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.ranking.pojo.RankingArenaTeamData;
import com.rwbase.dao.ranking.pojo.RankingTeamData;


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_ranking")
public class TableRanking {
	@Id
	private String key;
	private Map<String, RankingTeamData> fiveTeamList = new HashMap<String, RankingTeamData>();
	private Map<String, Map<String, RankingArenaTeamData>> arenaTeamList = new HashMap<String, Map<String, RankingArenaTeamData>>();
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Map<String, RankingTeamData> getFiveTeamList() {
		return fiveTeamList;
	}
	public void setFiveTeamList(Map<String, RankingTeamData> fiveTeamList) {
		this.fiveTeamList = fiveTeamList;
	}
	public Map<String, Map<String, RankingArenaTeamData>> getArenaTeamList() {
		return arenaTeamList;
	}
	public void setArenaTeamList(Map<String, Map<String, RankingArenaTeamData>> arenaTeamList) {
		this.arenaTeamList = arenaTeamList;
	}
}