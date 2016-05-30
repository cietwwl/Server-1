package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月28日 下午3:42:00
 * @Description 帮派技能配置表
 */
public class GroupSkillCfg {
	private int skillId;// 技能Id
	private String skillType;// 技能类型
	private String skillName;// 技能名字

	// private int isStartSkill;// 是否是研发起点技能

	/**
	 * 获取技能Id
	 * 
	 * @return
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * 获取技能类型
	 * 
	 * @return
	 */
	public String getSkillType() {
		return skillType;
	}

	/**
	 * 获取技能的名字
	 * 
	 * @return
	 */
	public String getSkillName() {
		return skillName;
	}

	// /**
	// * 是否研发起点技能
	// *
	// * @return
	// */
	// public boolean isStartSkill() {
	// return isStartSkill > 0;
	// }
}