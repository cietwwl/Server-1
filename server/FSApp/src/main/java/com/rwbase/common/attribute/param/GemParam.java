package com.rwbase.common.attribute.param;

import java.util.List;

/*
 * @author HC
 * @date 2016年5月14日 下午5:58:18
 * @Description 宝石计算属性需要的参数
 */
public class GemParam {
	private final String userId;
	private final String heroId;
	private final String heroModelId;
	private final List<String> gemList;

	private GemParam(String userId, String heroId, String heroModelId, List<String> gemList) {
		this.userId = userId;
		this.heroId = heroId;
		this.heroModelId = heroModelId;
		this.gemList = gemList;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public String getHeroModelId() {
		return heroModelId;
	}

	public List<String> getGemList() {
		return gemList;
	}

	public static class GemBuilder {
		private String userId;
		private String heroId;
		private String heroModelId;
		private List<String> gemList;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setHeroModelId(String heroModelId) {
			this.heroModelId = heroModelId;
		}

		public void setGemList(List<String> gemList) {
			this.gemList = gemList;
		}

		public GemParam build() {
			return new GemParam(userId, heroId, heroModelId, gemList);
		}
	}
}