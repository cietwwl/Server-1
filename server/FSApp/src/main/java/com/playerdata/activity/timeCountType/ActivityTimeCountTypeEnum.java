package com.playerdata.activity.timeCountType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityTimeCountTypeEnum{	// implements TypeIdentification
	role_online("401");
	
	private String cfgId;
	private ActivityTimeCountTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityTimeCountTypeEnum getById(String cfgId){
		ActivityTimeCountTypeEnum target = null;
		for (ActivityTimeCountTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
