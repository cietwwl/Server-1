package com.rwbase.common.attribute.param;

/*
 * @author HC
 * @date 2016年5月14日 下午6:35:26
 * @Description 
 */
public class MagicParam {
	private final String userId;
	private final String heroId;
	private final String magicId;
	private final int magicLevel;
	private final int magicAptitude;

	private MagicParam(String userId, String heroId, String magicId, int magicLevel, int magicAptitude) {
		this.userId = userId;
		this.heroId = heroId;
		this.magicId = magicId;
		this.magicLevel = magicLevel;
		this.magicAptitude = magicAptitude;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public String getMagicId() {
		return magicId;
	}

	public int getMagicLevel() {
		return magicLevel;
	}

	public int getMagicAptitude() {
		return magicAptitude;
	}

	public static class MagicBuilder {
		private String userId;
		private String heroId;
		private String magicId;
		private int magicLevel;
		private int magicAptitude;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setMagicId(String magicId) {
			this.magicId = magicId;
		}

		public void setMagicLevel(int magicLevel) {
			this.magicLevel = magicLevel;
		}
		
		public void setMagicAptitude(int magicAptitude){
			this.magicAptitude = magicAptitude;
		}

		public MagicParam build() {
			return new MagicParam(userId, heroId, magicId, magicLevel, magicAptitude);
		}
	}
}