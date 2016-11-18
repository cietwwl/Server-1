package com.rwbase.common.attribute.param;

import java.util.List;

import com.playerdata.team.SkillInfo;

/*
 * @author HC
 * @date 2016年5月14日 下午6:24:01
 * @Description 
 */
public class SkillParam {
	private final String userId;
	private final String heroTemplateId;
	private final List<SkillInfo> skillList;

	private SkillParam(String userId, String heroTemplateId, List<SkillInfo> skillList) {
		this.userId = userId;
		this.heroTemplateId = heroTemplateId;
		this.skillList = skillList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroTemplateId() {
		return heroTemplateId;
	}

	public List<SkillInfo> getSkillList() {
		return skillList;
	}

	public static class SkillBuilder {
		private String userId;
		private String heroTemplateId;
		private List<SkillInfo> skillList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroTemplateId(String heroId) {
			this.heroTemplateId = heroId;
		}

		public void setSkillList(List<SkillInfo> skillList) {
			this.skillList = skillList;
		}

		public SkillParam build() {
			return new SkillParam(userId, heroTemplateId, skillList);
		}
	}
}