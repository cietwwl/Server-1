package com.rwbase.common.teamsyn;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 下午12:25:28
 * @Description 
 */
@SynClass
public class DefendTeamInfoSynData {
	private final String userId;// 驻守角色的Id
	private final String headImageId;// 头像ID
	private final String name;// 名字
	private final int level;// 等级
	private final int defendFighting;// 驻守时的战斗力
	private final int magicId;// 法宝的模版Id
	private final int magicLevel;// 法宝的等级
	private final List<DefendHeroBaseInfoSynData> heroBaseInfo;// 驻守的英雄的基础信息
	private final int zoneId;// 区Id
	private final String zoneName;// 区名字
	private final String groupName;// 帮派名字

	public DefendTeamInfoSynData(String userId, String headImageId, String name, int level, int defendFighting, int magicId, int magicLevel, List<DefendHeroBaseInfoSynData> heroBaseInfo, int zoneId,
			String zoneName, String groupName) {
		this.userId = userId;
		this.headImageId = headImageId;
		this.name = name;
		this.level = level;
		this.defendFighting = defendFighting;
		this.magicId = magicId;
		this.magicLevel = magicLevel;
		this.heroBaseInfo = heroBaseInfo;
		this.zoneId = zoneId;
		this.zoneName = zoneName;
		this.groupName = groupName;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeadImageId() {
		return headImageId;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getDefendFighting() {
		return defendFighting;
	}

	public int getMagicId() {
		return magicId;
	}

	public int getMagicLevel() {
		return magicLevel;
	}

	public List<DefendHeroBaseInfoSynData> getHeroBaseInfo() {
		return heroBaseInfo;
	}

	public int getZoneId() {
		return zoneId;
	}

	public String getZoneName() {
		return zoneName;
	}

	public String getGroupName() {
		return groupName;
	}
}