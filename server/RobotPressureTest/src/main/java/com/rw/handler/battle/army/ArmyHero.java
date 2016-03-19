package com.rw.handler.battle.army;

import java.util.List;


/**
 * 战斗用临时数据，不能持久化
 * @author Administrator
 *
 */
public class ArmyHero {

	private CurAttrData curAttrData;
	private AttrData attrData;
	private List<Skill> skillList;
	private RoleBaseInfo roleBaseInfo;	
	private boolean isPlayer = false;
	private int fighting;//佣兵战斗力
	
	public AttrData getAttrData() {
		return attrData;
	}

	public List<Skill> getSkillList() {
		return skillList;
	}
	public RoleBaseInfo getRoleBaseInfo() {
		return roleBaseInfo;
	}
	public void setRoleBaseInfo(RoleBaseInfo roleBaseInfo) {
		this.roleBaseInfo = roleBaseInfo;
	}
	public int getFighting() {
		return fighting;
	}
	public void setFighting(int fighting) {
		this.fighting = fighting;
	}
	public void setAttrData(AttrData attrData) {
		this.attrData = attrData;
	}
	public void setSkillList(List<Skill> skillList) {
		this.skillList = skillList;
	}
	public boolean isPlayer() {
		return isPlayer;
	}
	public void setPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}
	public CurAttrData getCurAttrData() {
		return curAttrData;
	}
	public void setCurAttrData(CurAttrData curAttrData) {
		this.curAttrData = curAttrData;
	}

	
}
