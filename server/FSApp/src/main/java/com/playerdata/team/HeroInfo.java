package com.playerdata.team;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月15日 下午4:30:20
 * @Description 佣兵信息
 */
@SynClass
public class HeroInfo {
	private HeroBaseInfo baseInfo;
	private List<EquipInfo> equip;// 装备列表
	private List<SkillInfo> skill;// 技能列表
	private List<String> gem;// 宝石列表

	public HeroInfo() {
		baseInfo = new HeroBaseInfo();
		equip = new ArrayList<EquipInfo>();
		skill = new ArrayList<SkillInfo>();
		gem = new ArrayList<String>();
	}

	public List<EquipInfo> getEquip() {
		return equip;
	}

	public void setEquip(List<EquipInfo> equip) {
		this.equip = equip;
	}

	public List<SkillInfo> getSkill() {
		return skill;
	}

	public void setSkill(List<SkillInfo> skill) {
		this.skill = skill;
	}

	public HeroBaseInfo getBaseInfo() {
		return baseInfo;
	}

	public void setBaseInfo(HeroBaseInfo baseInfo) {
		this.baseInfo = baseInfo;
	}

	public List<String> getGem() {
		return gem;
	}

	public void setGem(List<String> gem) {
		this.gem = gem;
	}
}