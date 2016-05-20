package com.playerdata.activity.dailyCountType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityDailyCountTypeEnum{	// implements TypeIdentification
//	Login("11"),
//	GoldSpending("12"),
//	CopyWin("13"),
//	ElityCopyWin("14"),
//	BattleTower("15"),
//	GambleCoin("16"),
//	GambleGold("18"),
	Daily("201"),
	LoginDaily("20101"),
	TreasureLandDaily("20102"),//聚宝之地
	UpGradeStarDaily("20105"),//升星
	AdvanceDaily("20106"),//进阶
	BattleTowerDaily("20103"),//封神台
	ArenaDaily("20107"),//竞技场
	CoinSpendDaily("20108"),//花金币
	ChargeDaily("20110"),//充值
	GambleGoldDaily("20109"),//钻石钓鱼次数 
	AttachDaily("20104"),//附灵
	GoldSpendDaily("20111");//花钻石 
	
	
	
	private String cfgId;
	private ActivityDailyCountTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityDailyCountTypeEnum getById(String cfgId){
		ActivityDailyCountTypeEnum target = null;
		for (ActivityDailyCountTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
