package com.playerdata.activity.rateType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityRateTypeEnum{	
	// implements TypeIdentification
	ELITE_copy_DOUBLE("1"),
	Normal_copy_DOUBLE("2");

	
	private String cfgId;
	private ActivityRateTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityRateTypeEnum getById(String cfgId){
		ActivityRateTypeEnum target = null;
		for (ActivityRateTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
