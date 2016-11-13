package com.rwbase.common.attribute.param;

/*
 * @author HC
 * @date 2016年5月14日 下午6:35:26
 * @Description 
 */
public class MagicParam {
	private final String userId;
	private final String heroTemplateId;
	private final String magicId;
	private final int magicLevel;
	private final int magicAptitude;
	private final boolean isMainRole;

	private MagicParam(String userId, String heroTemplateId, String magicId, int magicLevel, int magicAptitude, boolean isMainRole) {
		this.userId = userId;
		this.heroTemplateId = heroTemplateId;
		this.magicId = magicId;
		this.magicLevel = magicLevel;
		this.magicAptitude = magicAptitude;
		this.isMainRole = isMainRole;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroTemplateId() {
		return heroTemplateId;
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
	
	public boolean isMainRole() {
		return isMainRole;
	}

	public static class MagicBuilder {
		private String userId;
		private String heroTemplateId;
		private String magicId;
		private int magicLevel;
		private int magicAptitude;
		private boolean isMainRole;

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setHeroTemplateId(String heroTemplateId) {
			this.heroTemplateId = heroTemplateId;
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
		
		public void setIsMainRole(boolean value) {
			this.isMainRole = value;
		}

		public MagicParam build() {
			return new MagicParam(userId, heroTemplateId, magicId, magicLevel, magicAptitude, isMainRole);
		}
	}
}