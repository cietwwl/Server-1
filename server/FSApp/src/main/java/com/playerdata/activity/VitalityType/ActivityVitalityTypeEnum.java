package com.playerdata.activity.VitalityType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityVitalityTypeEnum{	// implements TypeIdentification
	Vitality("801"),
	GoldSpendingVitality("80101"),//消费xx钻石
	GivePowerVitality("80102"),//赠送体力x次
	TreasureLandVitality("80103"),//聚宝之地通关x次
	TowerVitality("80104"),//万仙得xx徽记
	BattleTowerVitality("80105"),//封神通关第x层
	AttachVitality("80106"),//一件装备封灵到x级
	ResetElityVitality("80107"),//重置精英关卡x次
	HeroUpgradeVitality("80108"),//把一个英雄升到x级？？？？？
	WarfareDifficultyTwoVitality("80109"),//通关无尽战火难度2X次
	BuyInTowerShopVitality("80110"),//万仙阵商店购买x次
	BuyPowerVitality("80111"),//购买体力x次？？？？？
	FactionDonateVitality("80112"),//帮派捐献x次
	UseSweepTicketVitality("80113"),//使用扫荡券xx张
	LearnSkillInfactionVitality("80114"),//在帮派学习技能x次
	StrengthenMagicVitality("80115"),//强化法宝到x级
	UseSilverKeyVitality("80116"),//使用银钥匙x个
	GambleGoldVitality("80117"),//购买中级经验丹？？？
	ArenaVitality("80118"),//竞技场多少次
	
	VitalityTwo("810"),
	GoldSpendingVitalityTwo("81001"),//消费xx钻石
	GivePowerVitalityTwo("81002"),//赠送体力x次
	TreasureLandVitalityTwo("81003"),//聚宝之地通关x次
	TowerVitalityTwo("81004"),//万仙得xx徽记
	BattleTowerVitalityTwo("81005"),//封神通关第x层
	AttachVitalityTwo("81006"),//一件装备封灵到x级
	ResetElityVitalityTwo("81007"),//重置精英关卡x次
	HeroUpgradeVitalityTwo("81008"),//把一个英雄升到x级？？？？？
	WarfareDifficultyTwoVitalityTwo("81009"),//通关无尽战火难度2X次
	BuyInTowerShopVitalityTwo("81010"),//万仙阵商店购买x次
	BuyPowerVitalityTwo("81011"),//购买体力x次？？？？？
	FactionDonateVitalityTwo("81012"),//帮派捐献x次
	UseSweepTicketVitalityTwo("81013"),//使用扫荡券xx张
	LearnSkillInfactionVitalityTwo("81014"),//在帮派学习技能x次
	StrengthenMagicVitalityTwo("81015"),//强化法宝到x级
	UseSilverKeyVitalityTwo("81016"),//使用银钥匙x个
	GambleGoldVitalityTwo("81017"),//购买中级经验丹？？？
	ArenaVitalityTwo("81018");//竞技场多少次
	
	
	
	
	private String cfgId;
	private ActivityVitalityTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityVitalityTypeEnum getById(String cfgId){
		ActivityVitalityTypeEnum target = null;
		for (ActivityVitalityTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
