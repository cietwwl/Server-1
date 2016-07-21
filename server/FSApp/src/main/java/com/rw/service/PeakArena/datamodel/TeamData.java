package com.rw.service.PeakArena.datamodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.skill.pojo.TableSkill;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamData {

	private int teamId;
	private String magicId;
	private List<String> heros;
	private List<TableSkill> heroSkills;
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	
	public String getMagicId() {
		return magicId;
	}
	public void setMagicId(String magicId) {
		this.magicId = magicId;
	}
	public List<String> getHeros() {
		return heros;
	}
	public void setHeros(List<String> heros) {
		this.heros = heros;
	}
	public List<TableSkill> getHeroSkills() {
		return heroSkills;
	}
	public void setHeroSkills(List<TableSkill> heroSkills) {
		this.heroSkills = heroSkills;
	}
	
}
