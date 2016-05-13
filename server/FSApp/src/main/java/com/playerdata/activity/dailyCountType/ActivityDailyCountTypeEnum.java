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
	LoginDaily("1"),
	TreasureLandDaily("2"),//聚宝之地
	UpGradeStarDaily("3"),//升星
	AdvanceDaily("4"),//进阶
	BattleTowerDaily("5"),
	ArenaDaily("6"),
	CoinSpendDaily("7"),
	ChargeDaily("8"),
	GambleGoldDaily("9"),
	AttachDaily("10"),
	GoldSpendDaily("11");
	
	
	
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
