package com.rwbase.common.attribute.param;

import java.util.Map;

/*
 * @author HC
 * @date 2016年7月14日 下午12:23:26
 * @Description 
 */
public class GroupSkillParam {
	private final String userId;
	private final String heroId;
	private final Map<Integer, Integer> groupSkillMap;

	public GroupSkillParam(String userId, String heroId, Map<Integer, Integer> groupSkillMap) {
		this.userId = userId;
		this.heroId = heroId;
		this.groupSkillMap = groupSkillMap;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public Map<Integer, Integer> getGroupSkillMap() {
		return groupSkillMap;
	}

	public static class GroupSkillBuilder {
		private String userId;
		private String heroId;
		private Map<Integer, Integer> groupSkillMap;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setGroupSkillMap(Map<Integer, Integer> groupSkillMap) {
			this.groupSkillMap = groupSkillMap;
		}

		public GroupSkillParam build() {
			return new GroupSkillParam(userId, heroId, groupSkillMap);
		}
	}
}