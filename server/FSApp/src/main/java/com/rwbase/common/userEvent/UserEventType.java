package com.rwbase.common.userEvent;

public enum UserEventType {
	
	LOGIN,//钓鱼
	USE_GOLD,
	COPY_WIN,
	ELITE_COPY_WIN,
	BATTLETOWER,
	GAMBLE_COIN,
	GAMBLE_GOLD,
	CHARGE,
	
	LOGINDAILY,
	TREASURELANDDAILY,//聚宝之地
	UPGRADESTARDAILY,
	ADVANCEDAILY,
	BATTLETOWERDAILY,
	ARENADAILY,
	COINSPENDDAILY,
	CHARGEDAILY,
	GAMBLEGOLDDAILY,
	ATTACHDAILY,
	GOLDSPENDDAILY,
	
	GoldSpendingKingAttive,//消费xx钻石
	GivePowerKingAttive,//赠送体力x次
	TreasureLandKingAttive,//聚宝之地通关x次
	TowerKingAttive,//万仙得xx徽记
	BattleTowerKingAttive,//封神通关第x层
	AttachKingAttive,//一件装备封灵到x级
	ResetElityKingAttive,//重置精英关卡x次
	HeroUpgradeKingAttive,//把一个英雄升到x级
	WarfareDifficultyTwoKingAttive,//通关无尽战火难度2X次
	
	BuyInTowerShopKingAttive,//万仙阵商店购买x次
	BuyPowerKingAttive,//购买体力x次
	FactionDonateKingAttive,//帮派捐献x次
	UseSweepTicketKingAttive,//使用扫荡券xx张
	LearnSkillInfactionKingAttive,//在帮派学习技能x次
	StrengthenMagicKingAttive,//强化法宝到x级
	UseSilverKeyKingAttive,//使用银钥匙x个
	GambleGoldKingAttive,//购买中级经验丹
	arenaKingAttive;//竞技场多少次	
}
