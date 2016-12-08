package com.bm.robot;

import java.util.List;

import com.bm.arena.RobotEntryCfg;
import com.rwbase.common.RandomUtil;

public class RobotHeroBuildData {

	private int heroLevel;

	private int heroQuality;

	private int heroStar;

	private int roleModelId;

	// 装备数量
	private int heroEquipNum;
	// 装备附灵
	private int[] heroEnchant;

	// 宝石数量
	private int gemCount;
	// // 宝石等级
	// private int[] gemLevelArray;
	// ==宝石类型==
	private int[] gemTypeArray;

	// 技能
	private int[] heroFirstSkillLevel; // 佣兵第一个技能等级
	private int[] heroSecondSkillLevel;// 佣兵第二个技能等级
	private int[] heroThirdSkillLevel; // 佣兵第三个技能等级
	private int[] heroFourthSkillLevel;// 佣兵第四个技能等级
	private int[] heroFifthSkillLevel; // 佣兵第五个技能等级

	public static RobotHeroBuildData newHeroData(int roleModelId, RobotEntryCfg robotCfg, int mainRoleLevel) {

		RobotHeroBuildData buildData = new RobotHeroBuildData();

		buildData.roleModelId = roleModelId;
		// 等级
		List<Integer> level = robotCfg.getHeroLevel(mainRoleLevel);
		buildData.heroLevel = level.get(getRandomIndex(level.size()));
		// 品质
		int[] quality = robotCfg.getHeroQuality();
		buildData.heroQuality = quality[getRandomIndex(quality.length)];
		// 星级
		int[] star = robotCfg.getHeroStar();
		buildData.heroStar = star[getRandomIndex(star.length)];

		// 装备数量
		int[] equipNum = robotCfg.getHeroEquipments();
		buildData.heroEquipNum = equipNum[getRandomIndex(equipNum.length)];
		// 装备附灵
		buildData.heroEnchant = robotCfg.getHeroEnchant();

		// 宝石
		int[] gemCountArray = robotCfg.getHeroGemCount();
		// 宝石数量
		buildData.gemCount = gemCountArray[getRandomIndex(gemCountArray.length)];
		// // 宝石等级
		// buildData.gemLevelArray = robotCfg.getHeroGemLevel();
		// ==宝石类型==
		buildData.gemTypeArray = robotCfg.getHeroGemType();

		// 技能
		buildData.heroFirstSkillLevel = robotCfg.getHeroFirstSkillLevel(); // 佣兵第一个技能等级
		buildData.heroSecondSkillLevel = robotCfg.getHeroSecondSkillLevel();// 佣兵第二个技能等级
		buildData.heroThirdSkillLevel = robotCfg.getHeroThirdSkillLevel(); // 佣兵第三个技能等级
		buildData.heroFourthSkillLevel = robotCfg.getHeroFourthSkillLevel();// 佣兵第四个技能等级
		buildData.heroFifthSkillLevel = robotCfg.getHeroFifthSkillLevel(); // 佣兵第五个技能等级

		return buildData;
	}

	private static int getRandomIndex(int len) {
		return RandomUtil.nextInt(len);
	}

	public static RobotHeroBuildData newMainHeroData(int roleModelId, RobotEntryCfg robotCfg) {

		RobotHeroBuildData buildData = new RobotHeroBuildData();

		buildData.roleModelId = roleModelId;

		int[] level = robotCfg.getLevel();
		int mainRoleLevel = level[getRandomIndex(level.length)];

		// 等级
		buildData.heroLevel = mainRoleLevel;
		// 品质
		int[] quality = robotCfg.getQuality();
		buildData.heroQuality = quality[getRandomIndex(quality.length)];
		// 星级
		int[] star = robotCfg.getStar();
		buildData.heroStar = star[getRandomIndex(star.length)];

		// 装备数量
		int[] equipNum = robotCfg.getEquipments();
		buildData.heroEquipNum = equipNum[getRandomIndex(equipNum.length)];
		// 装备附灵
		buildData.heroEnchant = robotCfg.getEnchant();

		int[] gemCountArray = robotCfg.getGemCount();
		// 宝石数量
		buildData.gemCount = gemCountArray[getRandomIndex(gemCountArray.length)];
		// // 宝石等级
		// buildData.gemLevelArray = robotCfg.getGemLevel();
		// ==宝石类型==
		buildData.gemTypeArray = robotCfg.getGemType();

		// 技能
		buildData.heroFirstSkillLevel = robotCfg.getFirstSkillLevel(); // 佣兵第一个技能等级
		buildData.heroSecondSkillLevel = robotCfg.getSecondSkillLevel();// 佣兵第二个技能等级
		buildData.heroThirdSkillLevel = robotCfg.getThirdSkillLevel(); // 佣兵第三个技能等级
		buildData.heroFourthSkillLevel = robotCfg.getFourthSkillLevel();// 佣兵第四个技能等级
		buildData.heroFifthSkillLevel = robotCfg.getFifthSkillLevel(); // 佣兵第五个技能等级

		return buildData;
	}

	public String getTemplateId() {
		return roleModelId + "_" + heroStar;
	}

	public int getHeroLevel() {
		return heroLevel;
	}

	public int getHeroQuality() {
		return heroQuality;
	}

	public int getHeroStar() {
		return heroStar;
	}

	public int getRoleModelId() {
		return roleModelId;
	}

	public int getHeroEquipNum() {
		return heroEquipNum;
	}

	public int getGemCount() {
		return gemCount;
	}

	// public int[] getGemLevelArray() {
	// return gemLevelArray;
	// }

	public int[] getGemTypeArray() {
		return gemTypeArray;
	}

	public int[] getHeroEnchant() {
		return heroEnchant;
	}

	public int[] getHeroFirstSkillLevel() {
		return heroFirstSkillLevel;
	}

	public int[] getHeroSecondSkillLevel() {
		return heroSecondSkillLevel;
	}

	public int[] getHeroThirdSkillLevel() {
		return heroThirdSkillLevel;
	}

	public int[] getHeroFourthSkillLevel() {
		return heroFourthSkillLevel;
	}

	public int[] getHeroFifthSkillLevel() {
		return heroFifthSkillLevel;
	}

}
