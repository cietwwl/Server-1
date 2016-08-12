package com.rwbase.common.attribute.param;

/*
 * @author HC
 * @date 2016年7月14日 下午12:24:06
 * @Description 
 */
public class ExtraParam {
	private final String userId;
	private final String heroId;
	private final int extraAttrId;

	public ExtraParam(String userId, String heroId, int extraAttrId) {
		this.userId = userId;
		this.heroId = heroId;
		this.extraAttrId = extraAttrId;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public int getExtraAttrId() {
		return extraAttrId;
	}

	public static class ExtraBuilder {
		private String userId;
		private String heroId;
		private int extraAttrId;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setExtraAttrId(int extraAttrId) {
			this.extraAttrId = extraAttrId;
		}

		public ExtraParam build() {
			return new ExtraParam(userId, heroId, extraAttrId);
		}
	}
}