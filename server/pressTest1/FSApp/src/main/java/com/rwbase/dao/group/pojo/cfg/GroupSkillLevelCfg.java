package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月28日 下午3:45:39
 * @Description 技能配置表
 */
public class GroupSkillLevelCfg {
	private int skillId;// 技能Id
	private int skillLevel;// 技能等级
	private String researchCondation;// 研发技能的前提条件
	private int researchNeedSupply;// 研究技能的消耗
	private int studyNeedContribution;// 学习技能的个人消耗

	/**
	 * 获取技能Id
	 * 
	 * @return
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * 获取技能的等级
	 * 
	 * @return
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * 获取研发技能的条件
	 * 
	 * @return
	 */
	public String getResearchCondation() {
		return researchCondation;
	}

	/**
	 * 获取研发技能需要的帮派物资
	 * 
	 * @return
	 */
	public int getResearchNeedSupply() {
		return researchNeedSupply;
	}

	/**
	 * 获取学习技能需要的个人贡献
	 * 
	 * @return
	 */
	public int getStudyNeedContribution() {
		return studyNeedContribution;
	}
}