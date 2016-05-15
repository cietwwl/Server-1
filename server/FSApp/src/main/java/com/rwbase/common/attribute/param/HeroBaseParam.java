package com.rwbase.common.attribute.param;

/*
 * @author HC
 * @date 2016年5月14日 下午5:54:53
 * @Description 
 */
public class HeroBaseParam {
	private final String userId;
	private final String heroId;
	private final String heroTmpId;
	private final int level;
	private final String qualityId;

	private HeroBaseParam(String userId, String heroId, String heroTmpId, int level, String qualityId) {
		this.userId = userId;
		this.heroId = heroId;
		this.heroTmpId = heroTmpId;
		this.level = level;
		this.qualityId = qualityId;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public String getHeroTmpId() {
		return heroTmpId;
	}

	public int getLevel() {
		return level;
	}

	public String getQualityId() {
		return qualityId;
	}

	public static class HeroBaseBuilder {
		private String userId;
		private String heroId;
		private String heroTmpId;
		private int level;
		private String qualityId;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setHeroTmpId(String heroTmpId) {
			this.heroTmpId = heroTmpId;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public void setQualityId(String qualityId) {
			this.qualityId = qualityId;
		}

		public HeroBaseParam build() {
			return new HeroBaseParam(userId, heroId, heroTmpId, level, qualityId);
		}
	}
}