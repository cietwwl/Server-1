package com.rwbase.dao.hero.pojo;

public class HeroSkillRecord implements HeroSkillRecordIF{
	private int skillOrder;//技能位
	private int level;//技能等级
	public int getSkillOrder() {
		return skillOrder;
	}
	public void setSkillOrder(int skillOrder) {
		this.skillOrder = skillOrder;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
