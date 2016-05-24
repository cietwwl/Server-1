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
	private final String heroId;
	private final List<SkillInfo> skillList;

	private SkillParam(String userId, String heroId, List<SkillInfo> skillList) {
		this.userId = userId;
		this.heroId = heroId;
		this.skillList = skillList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public List<SkillInfo> getSkillList() {
		return skillList;
	}

	public static class SkillBuilder {
		private String userId;
		private String heroId;
		private List<SkillInfo> skillList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setSkillList(List<SkillInfo> skillList) {
			this.skillList = skillList;
		}

		public SkillParam build() {
			return new SkillParam(userId, heroId, skillList);
		}
	}
}