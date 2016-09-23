package com.rwbase.dao.hero.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class FSUserHeroGlobalData {

	@Id
	@JsonProperty("userId")
	private String userId;
	@JsonProperty("1")
	private int fightingTeam;
	@JsonProperty("2")
	private int startAll;
	@JsonProperty("3")
	private int fightingAll;
	@JsonProperty("4")
	private List<String> fightingTeamHeroIds = new ArrayList<String>();
	
	public FSUserHeroGlobalData() {}
	
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

	public List<String> getFightingTeamHeroIdsRO() {
		return Collections.unmodifiableList(fightingTeamHeroIds);
	}

	public void setFightingTeamHeroIds(List<String> fightingTeamHeroIds) {
		this.fightingTeamHeroIds = new ArrayList<String>(fightingTeamHeroIds);
	}
}
