package com.playerdata.activity.countType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityCountTypeEnum{	// implements TypeIdentification
	Login("11"),
	GoldSpending("12"),
	CopyWin("13"),
	ElityCopyWin("14"),
	BattleTower("15"),
	GambleCoin("16"),
	GambleGold("18"),
	LoginDaily("19"),
	TreasureLandDaily("20"),//聚宝之地
	UpGradeStarDaily("21"),//升星
	AdvanceDaily("22"),//进阶
	BattleTowerDaily("23"),
	ArenaDaily("24"),
	CoinSpendDaily("25"),
	ChargeDaily("26"),
	GambleGoldDaily("27"),
	AttachDaily("28"),
	GoldSpendDaily("29");
	
	
	
	private String cfgId;
	private ActivityCountTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityCountTypeEnum getById(String cfgId){
		ActivityCountTypeEnum target = null;
		for (ActivityCountTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
