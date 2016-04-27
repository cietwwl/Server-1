package com.playerdata.activity.timeCardType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityTimeCardTypeEnum{	// implements TypeIdentification
	Month("1"),
	Monthvip("2");
	
	private String cfgId;
	private ActivityTimeCardTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityTimeCardTypeEnum getById(String cfgId){
		ActivityTimeCardTypeEnum target = null;
		for (ActivityTimeCardTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
