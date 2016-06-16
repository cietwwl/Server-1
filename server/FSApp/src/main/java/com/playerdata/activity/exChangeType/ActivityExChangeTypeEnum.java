package com.playerdata.activity.exChangeType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityExChangeTypeEnum{	// implements TypeIdentification
	DragonBoatFestival("901"),
	MidAutumnFestival("902"),
	ExChangeActive("951");


	
	
	
	private String cfgId;
	private ActivityExChangeTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityExChangeTypeEnum getById(String cfgId){
		ActivityExChangeTypeEnum target = null;
		for (ActivityExChangeTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
