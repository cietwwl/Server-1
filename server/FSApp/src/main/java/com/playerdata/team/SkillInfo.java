package com.playerdata.team;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月15日 下午4:31:26
 * @Description 
 */
@SynClass
public class SkillInfo {
	private String skillId;// 技能Id
	private int skillLevel;// 技能等级

	/**
	 * <pre>
	 * 返回的技能Id格式为：
	 * 例如主角的某个技能Id：10000101
	 * 技能等级是：5
	 * 就传递参数是：10000101_5
	 * </pre>
	 * 
	 * @return
	 */
	public String getSkillId() {
		return skillId;
	}

	/**
	 * <pre>
	 * 设置skillId的格式是：skillModelId_skillLevel
	 * 例如主角的某个技能Id：10000101
	 * 技能等级是：5
	 * 就传递参数是：10000101_5
	 * </pre>
	 * 
	 * @param skillId
	 */
	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}
}