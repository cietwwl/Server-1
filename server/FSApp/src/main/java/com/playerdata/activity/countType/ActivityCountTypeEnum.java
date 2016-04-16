package com.playerdata.activity.countType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityCountTypeEnum {	
	Login("1");
	
	private String cfgId;
	private ActivityCountTypeEnum(String cfgIdP){
		this.cfgId = cfgIdP;
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
