package com.playerdata.activity.VitalityType;

import org.apache.commons.lang3.StringUtils;

public enum VitalityTypeEnum{	// implements TypeIdentification
	GoldSpendingKingAttive("80101"),//消费xx钻石
	GivePowerKingAttive("80102"),//赠送体力x次
	TreasureLandKingAttive("80103"),//聚宝之地通关x次
	TowerKingAttive("80104"),//万仙得xx徽记
	BattleTowerKingAttive("80105"),//封神通关第x层
	AttachKingAttive("80106"),//一件装备封灵到x级
	ResetElityKingAttive("80107"),//重置精英关卡x次
	HeroUpgradeKingAttive("80108"),//把一个英雄升到x级
	WarfareDifficultyTwoKingAttive("80109"),//通关无尽战火难度2X次
	BuyInTowerShopKingAttive("80110"),//万仙阵商店购买x次
	BuyPowerKingAttive("80111"),//购买体力x次
	FactionDonateKingAttive("80112"),//帮派捐献x次
	UseSweepTicketKingAttive("80113"),//使用扫荡券xx张
	LearnSkillInfactionKingAttive("80114"),//在帮派学习技能x次
	StrengthenMagicKingAttive("80115"),//强化法宝到x级
	UseSilverKeyKingAttive("80116"),//使用银钥匙x个
	GambleGoldKingAttive("80117"),//购买中级经验丹
	arenaKingAttive("80118");//竞技场多少次
	
	
	
	private String cfgId;
	private VitalityTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static VitalityTypeEnum getById(String cfgId){
		VitalityTypeEnum target = null;
		for (VitalityTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
