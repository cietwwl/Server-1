package com.rwbase.dao.fighting.pojo;

public class MagicSkillFightingCfg implements FightingByRequiredLv {
	
	private String key;
	private int requiredLv; // 需求等级
	private int activeSkillFighting; // 主动技能战斗力
	private int passiveSkillFighting; // 被动技能战斗力
	
	public String getKey() {
		return key;
	}
	
	public int getRequiredLv() {
		return requiredLv;
	}
	
	public int getActiveSkillFighting() {
		return activeSkillFighting;
	}
	
	public int getPassiveSkillFighting() {
		return passiveSkillFighting;
	}
}
