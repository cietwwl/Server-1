package com.playerdata.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.fetters.pojo.SynConditionData;

/*
 * @author HC
 * @date 2016年4月15日 下午4:30:20
 * @Description 佣兵信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class HeroInfo {
	private HeroBaseInfo baseInfo;
	private List<EquipInfo> equip;// 装备列表
	private List<SkillInfo> skill;// 技能列表
	private List<String> gem;// 宝石列表
	private Map<Integer, SynConditionData> fetters;// 英雄羁绊
	// 神器
	private List<HeroFixEquipInfo> fixEquip;// 神器

	public HeroInfo() {
		baseInfo = new HeroBaseInfo();
		equip = new ArrayList<EquipInfo>();
		skill = new ArrayList<SkillInfo>();
		gem = new ArrayList<String>();
		fetters = new HashMap<Integer, SynConditionData>();// 英雄羁绊
		fixEquip = new ArrayList<HeroFixEquipInfo>();// 神器
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

	public Map<Integer, SynConditionData> getFetters() {
		return fetters;
	}

	public void setFetters(Map<Integer, SynConditionData> fetters) {
		this.fetters = fetters;
	}

	public List<HeroFixEquipInfo> getFixEquip() {
		return fixEquip;
	}

	public void setFixEquip(List<HeroFixEquipInfo> fixEquip) {
		this.fixEquip = fixEquip;
	}
}