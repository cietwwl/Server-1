package com.rwbase.dao.groupsecret.syndata.base;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 下午12:18:08
 * @Description 驻守英雄的基础信息
 */
@SynClass
public class DefendHeroBaseInfoSynData {
	private final String headImageId;// 头像Id
	private final String qualityId;// 品质<直接发回的是RoleQualityCfg中的Key>
	private final int starLevel;// 星级
	private final int level;// 等级
	private final boolean isMainRole;// 是否是主角
	private final boolean isDie;// 是否死亡
	private final HeroLeftInfoSynData heroLeftInfo;// 剩余的血量能量信息

	public DefendHeroBaseInfoSynData(String headImageId, String qualityId, int starLevel, int level, boolean isMainRole, boolean isDie, HeroLeftInfoSynData heroLeftInfo) {
		this.headImageId = headImageId;
		this.qualityId = qualityId;
		this.starLevel = starLevel;
		this.level = level;
		this.isMainRole = isMainRole;
		this.isDie = isDie;
		this.heroLeftInfo = heroLeftInfo;
	}

	public String getHeadImageId() {
		return headImageId;
	}

	public String getQualityId() {
		return qualityId;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public int getLevel() {
		return level;
	}

	public boolean isMainRole() {
		return isMainRole;
	}

	public boolean isDie() {
		return isDie;
	}

	public HeroLeftInfoSynData getHeroLeftInfo() {
		return heroLeftInfo;
	}
}