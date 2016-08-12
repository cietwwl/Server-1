package com.rwbase.common.attribute.param;

/*
 * @author HC
 * @date 2016年7月14日 下午12:21:57
 * @Description 
 */
public class FashionParam {
	private final String userId;
	private final String heroId;
	private final int vaildCount;
	private final int[] fashionId;
	private final int career;

	public FashionParam(String userId, String heroId, int vaildCount, int[] fashionId, int career) {
		this.userId = userId;
		this.heroId = heroId;
		this.vaildCount = vaildCount;
		this.fashionId = fashionId;
		this.career = career;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public int getVaildCount() {
		return vaildCount;
	}

	public int[] getFashionId() {
		return fashionId;
	}

	public int getCareer() {
		return career;
	}

	public static class FashionBuilder {
		private String userId;
		private String heroId;
		private int vaildCount;
		private int[] fashionId;
		private int career;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setVaildCount(int vaildCount) {
			this.vaildCount = vaildCount;
		}

		public void setFashionId(int[] fashionId) {
			this.fashionId = fashionId;
		}

		public void setCareer(int career) {
			this.career = career;
		}

		public FashionParam build() {
			return new FashionParam(userId, heroId, vaildCount, fashionId, career);
		}
	}
}