package com.playerdata.army;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.skill.pojo.Skill;


/**
 * 战斗用临时数据，不能持久化
 * @author Administrator
 *
 */

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyHero {

	private CurAttrData curAttrData;
	private AttrData attrData;
	private List<Skill> skillList;
	private RoleBaseInfo roleBaseInfo;	
	private boolean isPlayer = false;
	private int fighting;//佣兵战斗力
	
	public ArmyHero(){}
	public ArmyHero(RoleBaseInfo roleBaseInfoP, AttrData attrDataP, List<Skill> skillListP){
		this.roleBaseInfo = roleBaseInfoP;
		this.attrData = attrDataP;
		this.skillList = skillListP;
	}
	
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
