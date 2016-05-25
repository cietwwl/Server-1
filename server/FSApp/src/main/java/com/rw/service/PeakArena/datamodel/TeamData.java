package com.rw.service.PeakArena.datamodel;

import java.util.List;

import com.rwbase.common.attrdata.TableAttr;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.skill.pojo.TableSkill;

public class TeamData {

	private int teamId;
	private int magicId;
	private int magicLevel;
	private List<RoleBaseInfo> heros;
	private List<TableSkill> heroSkills;
	private List<TableAttr> heroAtrrs;
	
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
	public List<RoleBaseInfo> getHeros() {
		return heros;
	}
	public void setHeros(List<RoleBaseInfo> heros) {
		this.heros = heros;
	}
	public List<TableSkill> getHeroSkills() {
		return heroSkills;
	}
	public void setHeroSkills(List<TableSkill> heroSkills) {
		this.heroSkills = heroSkills;
	}
	public List<TableAttr> getHeroAtrrs() {
		return heroAtrrs;
	}
	public void setHeroAtrrs(List<TableAttr> heroAtrrs) {
		this.heroAtrrs = heroAtrrs;
	}
	
}
