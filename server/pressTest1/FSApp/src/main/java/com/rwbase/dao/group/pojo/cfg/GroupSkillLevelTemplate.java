package com.rwbase.dao.group.pojo.cfg;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/*
 * @author HC
 * @date 2016年1月28日 下午4:28:45
 * @Description 帮派技能的等级模版
 */
public class GroupSkillLevelTemplate {
	private final int skillId;// 技能Id
	private final int skillLevel;// 技能等级
	private final Map<Integer, Integer> researchCondation;// 研发技能的前提条件
	private final int researchNeedSupply;// 研究技能的消耗
	private final int studyNeedContribution;// 学习技能的个人消耗

	public GroupSkillLevelTemplate(GroupSkillLevelCfg cfg) {
		this.skillId = cfg.getSkillId();
		this.skillLevel = cfg.getSkillLevel();
		this.researchNeedSupply = cfg.getResearchNeedSupply();
		this.studyNeedContribution = cfg.getStudyNeedContribution();

		String condation = cfg.getResearchCondation();
		if (!StringUtils.isEmpty(condation)) {
			String[] strArr = condation.split(";");
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(strArr.length);
			for (String s : strArr) {
				String[] sArr = s.split("_");
				map.put(Integer.valueOf(sArr[0]), Integer.valueOf(sArr[1]));
			}

			researchCondation = Collections.unmodifiableMap(map);
		} else {
			researchCondation = Collections.emptyMap();
		}
	}

	/**
	 * 获取技能Id
	 * 
	 * @return
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * 获取技能等级
	 * 
	 * @return
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * 获取研究技能的前置条件
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getResearchCondation() {
		return researchCondation;
	}

	/**
	 * 获取研究技能需要的帮派物资
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