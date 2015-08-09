package com.playerdata.activity.countType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityCountTypeEnum{	// implements TypeIdentification
	Login("1"),
	GoldSpending("2"),
	CopyWin("3"),
	ElityCopyWin("4"),
	BattleTower("5"),
	GambleCoin("6"),
	Charge("7"),
	GambleGold("8");

	
	
	
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
