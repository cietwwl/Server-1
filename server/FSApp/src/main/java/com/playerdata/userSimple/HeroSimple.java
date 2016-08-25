package com.playerdata.userSimple;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;
import com.rwbase.dao.skill.pojo.SkillItem;


/**
 * 战斗用临时数据，不能持久化
 * @author Administrator
 *
 */
@SynClass
public class HeroSimple {

	private AttrData attrData;
	private List<SkillItem> skillList;
	private RoleBaseInfoIF roleBaseInfo;	
	private boolean isPlayer = false;
	private int fighting;//佣兵战斗力
	
	public HeroSimple(){}
	public HeroSimple(RoleBaseInfoIF roleBaseInfoP, AttrData attrDataP, List<SkillItem> skillListP){
		this.roleBaseInfo = roleBaseInfoP;
		this.attrData = attrDataP;
		this.skillList = skillListP;
	}
	
	public AttrData getAttrData() {
		return attrData;
	}

	public List<SkillItem> getSkillList() {
		return skillList;
	}
	public RoleBaseInfoIF getRoleBaseInfo() {
		return roleBaseInfo;
	}
	public void setRoleBaseInfo(RoleBaseInfoIF roleBaseInfo) {
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
	public void setSkillList(List<SkillItem> skillList) {
		this.skillList = skillList;
	}
	public boolean isPlayer() {
		return isPlayer;
	}
	public void setPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}

	
}
