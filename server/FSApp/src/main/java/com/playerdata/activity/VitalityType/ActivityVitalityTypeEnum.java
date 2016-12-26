package com.playerdata.activity.VitalityType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityVitalityTypeEnum{	// implements TypeIdentification
	Vitality("59999"),
	GoldSpendingVitality("50001"),//消费xx钻石
	GivePowerVitality("50002"),//赠送体力x次
	TreasureLandVitality("50003"),//聚宝之地通关x次
	TowerVitality("50004"),//万仙得xx徽记
	BattleTowerVitality("50005"),//封神通关第x层
	AttachVitality("50006"),//一件装备封灵到x级
	ResetElityVitality("50007"),//重置精英关卡x次
	HeroUpgradeVitality("50008"),//把一个英雄升到x级？？？？？
	WarfareDifficultyTwoVitality("50009"),//通关无尽战火难度2X次
	BuyInTowerShopVitality("50010"),//万仙阵商店购买x次
	BuyPowerVitality("50011"),//购买体力x次？？？？？
	FactionDonateVitality("50012"),//帮派捐献x次
	UseSweepTicketVitality("50013"),//使用扫荡券xx张
	LearnSkillInfactionVitality("50014"),//在帮派学习技能x次
	StrengthenMagicVitality("50015"),//强化法宝到x级
	UseSilverKeyVitality("50016"),//使用银钥匙x个
	GambleGoldVitality("50017"),//购买中级经验丹？？？
	ArenaVitality("50018"),//竞技场多少次
	
	VitalityTwo("59998"),
	GoldSpendingVitalityTwo("51001"),//消费xx钻石
	GivePowerVitalityTwo("51002"),//赠送体力x次
	TreasureLandVitalityTwo("51003"),//聚宝之地通关x次
	TowerVitalityTwo("51004"),//万仙得xx徽记
	BattleTowerVitalityTwo("51005"),//封神通关第x层
	AttachVitalityTwo("51006"),//一件装备封灵到x级
	ResetElityVitalityTwo("51007"),//重置精英关卡x次
	HeroUpgradeVitalityTwo("51008"),//把一个英雄升到x级？？？？？
	WarfareDifficultyTwoVitalityTwo("51009"),//通关无尽战火难度2X次
	BuyInTowerShopVitalityTwo("51010"),//万仙阵商店购买x次
	BuyPowerVitalityTwo("51011"),//购买体力x次？？？？？
	FactionDonateVitalityTwo("51012"),//帮派捐献x次
	UseSweepTicketVitalityTwo("51013"),//使用扫荡券xx张
	LearnSkillInfactionVitalityTwo("51014"),//在帮派学习技能x次
	StrengthenMagicVitalityTwo("51015"),//强化法宝到x级
	UseSilverKeyVitalityTwo("51016"),//使用银钥匙x个
	GambleGoldVitalityTwo("51017"),//购买中级经验丹？？？
	ArenaVitalityTwo("51018");//竞技场多少次
	
	
	
	
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
