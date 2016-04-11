package com.rwbase.dao.battletower.pojo;

import com.rwbase.dao.battletower.pojo.readonly.BattleTowerHeroInfoIF;

/*
 * @author HC
 * @date 2015年9月2日 下午5:50:38
 * @Description 佣兵的信息
 */
public class BattleTowerHeroInfo implements BattleTowerHeroInfoIF {
	private String heroId;// 佣兵的资源Id
	private int level;// 佣兵当时的等级
	private int quality;// 佣兵当时的品质
	private int starNum;// 佣兵当时的星数
	private boolean isMainRole;

	public boolean isMainRole() {
		return isMainRole;
	}

	public void setMainRole(boolean isMainRole) {
		this.isMainRole = isMainRole;
	}

	public String getHeroId() {
		return heroId;
	}

	public void setHeroId(String heroId) {
		this.heroId = heroId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getStarNum() {
		return starNum;
	}

	public void setStarNum(int starNum) {
		this.starNum = starNum;
	}
}