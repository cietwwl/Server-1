package com.bm.arena;

public class RobotCfg {

	/** 竞技场机器人类型为1，万仙阵机器人为2 */
	private int robotType;// 机器人的类型
	private String ranking; // 排名
	private String level; // 主角等级
	private String quality; // 主角品质
	private String star; // 主角星级
	private String equipments; // 主角装备数量
	private String enchant; // 主角附灵星级
	private String gemCount; // 主角宝石数量
	private String gemType; // 主角宝石种类
	// private String gemLevel; // 主角宝石等级
	private String firstSkillLevel;// 第一个技能等级
	private String secondSkillLevel;// 第二个技能等级
	private String thirdSkillLevel; // 第三个技能等级
	private String fourthSkillLevel;// 第四个技能等级
	private String fifthSkillLevel; // 第五个技能等级
	private String fashions; // 主角时装
	private String vipLevel; // 主角vip等级
	private String magicId; // 主角法宝id
	private String magicLevel; // 法宝技能等级
	private String heroGroupId; // 佣兵组合ID
	private String heroLevel; // 佣兵等级
	private String heroQuality; // 佣兵品质
	private String heroStar; // 佣兵星级
	private String heroEquipments; // 佣兵装备
	private String heroEnchant; // 佣兵附灵
	private String heroGemCount; // 佣兵宝石数量
	private String heroGemType; // 佣兵宝石种类
	// private String heroGemLevel; // 佣兵宝石等级
	private String heroFirstSkillLevel; // 佣兵第一个技能等级
	private String heroSecondSkillLevel;// 佣兵第二个技能等级
	private String heroThirdSkillLevel; // 佣兵第三个技能等级
	private String heroFourthSkillLevel;// 佣兵第四个技能等级
	private String heroFifthSkillLevel; // 佣兵第五个技能等级
	// 增加的机器人数据
	private String fixEquipLevel;// 神器的等级
	private String fixEquipQuality;// 神器的品质
	private String fixEquipStar;// 神器的星数
	private String taoistLevel;// 道术的等级
	private String heroFetters;// 羁绊的等级
	private int extraAttrId;// 额外增加的属性Id
	private String limitValue;// 限定范围的值

	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getEquipments() {
		return equipments;
	}

	public void setEquipments(String equipments) {
		this.equipments = equipments;
	}

	public String getEnchant() {
		return enchant;
	}

	public void setEnchant(String enchant) {
		this.enchant = enchant;
	}

	public String getGemCount() {
		return gemCount;
	}

	public void setGemCount(String gemCount) {
		this.gemCount = gemCount;
	}

	public String getGemType() {
		return gemType;
	}

	public void setGemType(String gemType) {
		this.gemType = gemType;
	}

	// public String getGemLevel() {
	// return gemLevel;
	// }
	//
	// public void setGemLevel(String gemLevel) {
	// this.gemLevel = gemLevel;
	// }

	public String getFirstSkillLevel() {
		return firstSkillLevel;
	}

	public void setFirstSkillLevel(String firstSkillLevel) {
		this.firstSkillLevel = firstSkillLevel;
	}

	public String getSecondSkillLevel() {
		return secondSkillLevel;
	}

	public void setSecondSkillLevel(String secondSkillLevel) {
		this.secondSkillLevel = secondSkillLevel;
	}

	public String getThirdSkillLevel() {
		return thirdSkillLevel;
	}

	public void setThirdSkillLevel(String thirdSkillLevel) {
		this.thirdSkillLevel = thirdSkillLevel;
	}

	public String getFourthSkillLevel() {
		return fourthSkillLevel;
	}

	public void setFourthSkillLevel(String fourthSkillLevel) {
		this.fourthSkillLevel = fourthSkillLevel;
	}

	public String getFifthSkillLevel() {
		return fifthSkillLevel;
	}

	public void setFifthSkillLevel(String fifthSkillLevel) {
		this.fifthSkillLevel = fifthSkillLevel;
	}

	public String getFashions() {
		return fashions;
	}

	public void setFashions(String fashions) {
		this.fashions = fashions;
	}

	public String getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(String vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getMagicId() {
		return magicId;
	}

	public void setMagicId(String magicId) {
		this.magicId = magicId;
	}

	public String getMagicLevel() {
		return magicLevel;
	}

	public void setMagicLevel(String magicLevel) {
		this.magicLevel = magicLevel;
	}

	public String getHeroGroupId() {
		return heroGroupId;
	}

	public void setHeroGroupId(String heroGroupId) {
		this.heroGroupId = heroGroupId;
	}

	public String getHeroLevel() {
		return heroLevel;
	}

	public void setHeroLevel(String heroLevel) {
		this.heroLevel = heroLevel;
	}

	public String getHeroQuality() {
		return heroQuality;
	}

	public void setHeroQuality(String heroQuality) {
		this.heroQuality = heroQuality;
	}

	public String getHeroStar() {
		return heroStar;
	}

	public void setHeroStar(String heroStar) {
		this.heroStar = heroStar;
	}

	public String getHeroEquipments() {
		return heroEquipments;
	}

	public void setHeroEquipments(String heroEquipments) {
		this.heroEquipments = heroEquipments;
	}

	public String getHeroEnchant() {
		return heroEnchant;
	}

	public void setHeroEnchant(String heroEnchant) {
		this.heroEnchant = heroEnchant;
	}

	public String getHeroGemCount() {
		return heroGemCount;
	}

	public void setHeroGemCount(String heroGemCount) {
		this.heroGemCount = heroGemCount;
	}

	public String getHeroGemType() {
		return heroGemType;
	}

	public void setHeroGemType(String heroGemType) {
		this.heroGemType = heroGemType;
	}

	// public String getHeroGemLevel() {
	// return heroGemLevel;
	// }
	//
	// public void setHeroGemLevel(String heroGemLevel) {
	// this.heroGemLevel = heroGemLevel;
	// }

	public String getHeroFirstSkillLevel() {
		return heroFirstSkillLevel;
	}

	public void setHeroFirstSkillLevel(String heroFirstSkillLevel) {
		this.heroFirstSkillLevel = heroFirstSkillLevel;
	}

	public String getHeroSecondSkillLevel() {
		return heroSecondSkillLevel;
	}

	public void setHeroSecondSkillLevel(String heroSecondSkillLevel) {
		this.heroSecondSkillLevel = heroSecondSkillLevel;
	}

	public String getHeroThirdSkillLevel() {
		return heroThirdSkillLevel;
	}

	public void setHeroThirdSkillLevel(String heroThirdSkillLevel) {
		this.heroThirdSkillLevel = heroThirdSkillLevel;
	}

	public String getHeroFourthSkillLevel() {
		return heroFourthSkillLevel;
	}

	public void setHeroFourthSkillLevel(String heroFourthSkillLevel) {
		this.heroFourthSkillLevel = heroFourthSkillLevel;
	}

	public String getHeroFifthSkillLevel() {
		return heroFifthSkillLevel;
	}

	public void setHeroFifthSkillLevel(String heroFifthSkillLevel) {
		this.heroFifthSkillLevel = heroFifthSkillLevel;
	}

	public int getRobotType() {
		return robotType;
	}

	public void setRobotType(int robotType) {
		this.robotType = robotType;
	}

	public String getFixEquipLevel() {
		return fixEquipLevel;
	}

	public String getFixEquipQuality() {
		return fixEquipQuality;
	}

	public String getFixEquipStar() {
		return fixEquipStar;
	}

	public String getTaoistLevel() {
		return taoistLevel;
	}

	public String getHeroFetters() {
		return heroFetters;
	}

	public int getExtraAttrId() {
		return extraAttrId;
	}

	public String getLimitValue() {
		return limitValue;
	}
}