package com.rw.dataaccess.hero;


public class HeroCreateParam {

	private final String userId; // 玩家id
	private final String heroId; // 英雄id
	private final String qualityId; // 品质id
	private final int playerLevel; // 玩家等级
	private final int heroLevel; // 英雄等级
	private final int modelId;		//模型id
	

	public HeroCreateParam(String userId, String heroId, String qualityId, int playerLevel, int heroLevel, int modelId) {
		super();
		this.userId = userId;
		this.heroId = heroId;
		this.qualityId = qualityId;
		this.playerLevel = playerLevel;
		this.heroLevel = heroLevel;
		this.modelId = modelId;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeroId() {
		return heroId;
	}

	public String getQualityId() {
		return qualityId;
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public int getHeroLevel() {
		return heroLevel;
	}

	public int getModelId() {
		return modelId;
	}
	

}
