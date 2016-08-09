package com.playerdata.activity.countType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityCountTypeEnum{	// implements TypeIdentification
	Login("11"),
	GoldSpending("12"),
	CopyWin("13"),
	ElityCopyWin("14"),
	BattleTower("15"),
	GambleCoin("16"),
	Charge("17"),
	GambleGold("18");

	
	
	
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
