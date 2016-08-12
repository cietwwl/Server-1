package com.rwbase.common.attribute.param;

import java.util.Map;

/*
 * @author HC
 * @date 2016年7月14日 下午12:00:16
 * @Description 
 */
public class TaoistParam {
	private final String userId;
	private final String heroId;
	private final Map<Integer, Integer> taoistMap;

	public TaoistParam(String userId, String heroId, Map<Integer, Integer> taoistMap) {
		this.userId = userId;
		this.heroId = heroId;
		this.taoistMap = taoistMap;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public Map<Integer, Integer> getTaoistMap() {
		return taoistMap;
	}

	public static class TaoistBuilder {
		private String userId;
		private String heroId;
		private Map<Integer, Integer> taoistMap;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setTaoistMap(Map<Integer, Integer> taoistMap) {
			this.taoistMap = taoistMap;
		}

		public TaoistParam build() {
			return new TaoistParam(userId, heroId, taoistMap);
		}
	}
}