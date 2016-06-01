package com.rw.service.PeakArena.datamodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.skill.pojo.TableSkill;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamData {

	private int teamId;
	private int magicId;
	private int magicLevel;
	private List<String> heros;
	private List<TableSkill> heroSkills;
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	
	public int getMagicId() {
		return magicId;
	}
	public void setMagicId(int magicId) {
		this.magicId = magicId;
	}
	public int getMagicLevel() {
		return magicLevel;
	}
	public void setMagicLevel(int magicLevel) {
		this.magicLevel = magicLevel;
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
