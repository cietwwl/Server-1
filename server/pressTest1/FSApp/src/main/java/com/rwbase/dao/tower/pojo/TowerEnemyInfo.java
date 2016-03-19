package com.rwbase.dao.tower.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.TableSkill;

public class TowerEnemyInfo implements TowerEnemyInfoIF{
	private String userId;//敌方所属用户id
	private int towerId;
	
	private AttrData playerAttrData;//主角总属性汇总
	private List<Skill>  playerSkillList;//主角技能
	private List<TableTowerHeroData> heros = new ArrayList<TableTowerHeroData>();
	

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Skill> getPlayerSkill() {
		return playerSkillList;
	}

	public void setPlayerSkill(List<Skill> playerSkill) {
		this.playerSkillList = playerSkill;
	}
	
	public int getTowerId() {
		return towerId;
	}


	public void setTowerId(int towerId) {
		this.towerId = towerId;
	}


	public List<TableTowerHeroData> getHeros() {
		return heros;
	}


	public void setHeros(List<TableTowerHeroData> heros) {
		this.heros = heros;
	}


	public AttrData getPlayerAttrData() {
		return playerAttrData;
	}


	public void setPlayerAttrData(AttrData playerAttrData) {
		this.playerAttrData = playerAttrData;
	}


	public List<Skill> getPlayerSkillList() {
		return playerSkillList;
	}


	public void setPlayerSkillList(List<Skill> playerSkillList) {
		this.playerSkillList = playerSkillList;
	}


	
}
