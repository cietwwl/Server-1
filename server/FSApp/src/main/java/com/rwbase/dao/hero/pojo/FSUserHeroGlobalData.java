package com.rwbase.dao.hero.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class FSUserHeroGlobalData {

	@Id
	@IgnoreSynField
	@JsonProperty("userId")
	private String userId;
	@JsonProperty("1")
	private int fightingTeam;
	@JsonProperty("2")
	private int startAll;
	@JsonProperty("3")
	private int fightingAll;
//	@IgnoreSynField
//	@JsonProperty("4")
//	private List<String> fightingTeamHeroIds = new ArrayList<String>();
	@IgnoreSynField
	@JsonProperty("5")
	private Map<String, Integer> fightingTeamInfos = new HashMap<String, Integer>();

	public FSUserHeroGlobalData() {
	}

	public FSUserHeroGlobalData(String userId) {
		this.userId = userId;
	}

	public int getFightingTeam() {
		return fightingTeam;
	}

	public void setFightingTeam(int fightingTeam) {
		this.fightingTeam = fightingTeam;
	}

	public int getStartAll() {
		return startAll;
	}

	public void setStartAll(int startAll) {
		this.startAll = startAll;
	}

	public int getFightingAll() {
		return fightingAll;
	}

	public void setFightingAll(int fightingAll) {
		this.fightingAll = fightingAll;
	}

//	public List<String> getFightingTeamHeroIdsRO() {
//		return Collections.unmodifiableList(fightingTeamHeroIds);
//	}
//
//	public void setFightingTeamHeroIds(List<String> fightingTeamHeroIds) {
//		this.fightingTeamHeroIds = new ArrayList<String>(fightingTeamHeroIds);
//	}
	
	public Map<String, Integer> getFightingTeamInfosRO() {
		return Collections.unmodifiableMap(fightingTeamInfos);
	}
	
	public void setFightingTeamInfos(Map<String, Integer> map) {
		this.fightingTeamInfos = new HashMap<String, Integer>(map);
	}
	
	public Integer getFighting(String heroId) {
		return fightingTeamInfos.get(heroId);
	}
	
	public void updateHeroFighting(String heroId, int nowFighting) {
		if(fightingTeamInfos.containsKey(heroId)) {
			fightingTeamInfos.put(heroId, nowFighting);
		}
	}

	public String getUserId() {
		return userId;
	}
}