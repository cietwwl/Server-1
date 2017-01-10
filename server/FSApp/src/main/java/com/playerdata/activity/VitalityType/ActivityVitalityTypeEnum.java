package com.playerdata.activity.VitalityType;

import java.util.HashMap;


public enum ActivityVitalityTypeEnum{	// implements TypeIdentification

	GoldSpendingVitality("51001", true),//消费xx钻石
	GivePowerVitality("51002", true),//赠送体力x次
	TreasureLandVitality("51003", true),//聚宝之地通关x次
	TowerVitality("51004", true),//万仙得xx徽记
	BattleTowerVitality("51005", false),//封神通关第x层
	AttachVitality("51006", false),//附灵到x级
	ResetElityVitality("51007", true),//重置精英关卡x次
	HeroUpgradeVitality("51008", false),//把一个英雄升到x级
	BuyInTowerShopVitality("51010", true),//万仙阵商店购买x次
	BuyPowerVitality("51011", true),//购买体力x次
	FactionDonateVitality("51012", true),//帮派捐献x次
	UseSweepTicketVitality("51013", true),//使用扫荡券xx张
	LearnSkillInfactionVitality("51014", true),//在帮派学习技能x次
	StrengthenMagicVitality("51015", false),//强化法宝到x级
	UseSilverKeyVitality("51016", true),//使用银钥匙x个
	GambleGoldVitality("51017", true),//钻石抽x次
	ArenaVitality("51018", true);//竞技场x次
	
	
	private String cfgId;
	private boolean isAdd;	//是添加数量还是设置数量
	private ActivityVitalityTypeEnum(String cfgId, boolean isAdd){
		this.cfgId = cfgId;
		this.isAdd = isAdd;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	public boolean isAdd(){
		return isAdd;
	}
	
	private static HashMap<String, ActivityVitalityTypeEnum> map;

	static {
		ActivityVitalityTypeEnum[] array = values();
		map = new HashMap<String, ActivityVitalityTypeEnum>();
		for (int i = 0; i < array.length; i++) {
			ActivityVitalityTypeEnum typeEnum = array[i];
			map.put(typeEnum.getCfgId(), typeEnum);
		}
	}

	public static ActivityVitalityTypeEnum getById(String cfgId) {
		return map.get(cfgId);
	}
	
}
