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
	UpGradeStarDaily("20103"),//升星
	AdvanceDaily("20104"),//进阶
	BattleTowerDaily("20105"),//???
	ArenaDaily("20106"),//???
	CoinSpendDaily("20107"),
	ChargeDaily("20108"),
	GambleGoldDaily("20109"),
	AttachDaily("20110"),
	GoldSpendDaily("20111");
	
	
	
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
