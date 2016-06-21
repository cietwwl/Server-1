package com.playerdata.activity.rankType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityRankTypeEnum{	// implements TypeIdentification
	
	FIGHTING("1001"),//战力大比拼
	ARENA("1002");//竞技之王
	
	
	private String cfgId;
	
	private ActivityRankTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityRankTypeEnum getById(String cfgId){
		ActivityRankTypeEnum target = null;
		for (ActivityRankTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
