package com.rwbase.common.attribute.param;

import java.util.Map;

import com.rwbase.dao.fetters.pojo.SynConditionData;

/*
 * @author HC
 * @date 2016年7月14日 下午12:23:05
 * @Description 
 */
public class FettersParam {
	private final String userId;
	private final String heroId;
	private final Map<Integer, SynConditionData> openMap;

	public FettersParam(String userId, String heroId, Map<Integer, SynConditionData> openMap) {
		this.userId = userId;
		this.heroId = heroId;
		this.openMap = openMap;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public Map<Integer, SynConditionData> getOpenMap() {
		return openMap;
	}

	public static class FettersBuilder {
		private String userId;
		private String heroId;
		private Map<Integer, SynConditionData> openMap;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setOpenMap(Map<Integer, SynConditionData> openMap) {
			this.openMap = openMap;
		}

		public FettersParam build() {
			return new FettersParam(userId, heroId, openMap);
		}
	}
}