package com.rwbase.dao.user.pojo;


public class LevelCfg {
	
	private int level; // 等级
	private int playerUpgradeExp; // 升级经验
	private int heroUpgradeExp; // 升级经验
	
	public LevelCfg() {
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPlayerUpgradeExp() {
		return playerUpgradeExp;
	}

	public void setPlayerUpgradeExp(int playerUpgradeExp) {
		this.playerUpgradeExp = playerUpgradeExp;
	}

	public int getHeroUpgradeExp() {
		return heroUpgradeExp;
	}

	public void setHeroUpgradeExp(int heroUpgradeExp) {
		this.heroUpgradeExp = heroUpgradeExp;
	}
}
