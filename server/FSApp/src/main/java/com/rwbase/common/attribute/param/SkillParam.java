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
	private final List<SkillInfo> skillList;

	private SkillParam(String userId, List<SkillInfo> skillList) {
		this.userId = userId;
		this.skillList = skillList;
	}

	public String getUserId() {
		return userId;
	}

	public List<SkillInfo> getSkillList() {
		return skillList;
	}

	public static class SkillBuilder {
		private String userId;
		private List<SkillInfo> skillList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setSkillList(List<SkillInfo> skillList) {
			this.skillList = skillList;
		}

		public SkillParam build() {
			return new SkillParam(userId, skillList);
		}
	}
}