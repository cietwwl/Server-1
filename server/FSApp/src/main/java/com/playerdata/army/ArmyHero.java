package com.playerdata.army;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.hero.core.RoleBaseInfo;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfoIF;
import com.rwbase.dao.skill.pojo.SkillItem;

/**
 * 战斗用临时数据，不能持久化
 * 
 * @author Administrator
 *
 */

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyHero {

	private CurAttrData curAttrData;
	private AttrData attrData;
	private List<SkillItem> skillList;
//	private RoleBaseInfo roleBaseInfo;
	private RoleBaseInfo roleBaseInfo;
	private boolean isPlayer = false;
	private int fighting;// 佣兵战斗力
	private int position;// 英雄的站位

	public ArmyHero() {
	}

	public ArmyHero(RoleBaseInfoIF roleBaseInfoP, AttrData attrDataP, List<SkillItem> skillListP) {
		this.roleBaseInfo = new RoleBaseInfo(roleBaseInfoP);
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

	public void setSkillList(List<SkillItem> skillList) {
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}