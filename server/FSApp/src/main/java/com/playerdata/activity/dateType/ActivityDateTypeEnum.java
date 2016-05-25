package com.playerdata.activity.dateType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityDateTypeEnum{	// implements TypeIdentification
	
	DATE_CHARGE("1");//连续充值活动
	
	
	private String cfgId;
	
	private ActivityDateTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityDateTypeEnum getById(String cfgId){
		ActivityDateTypeEnum target = null;
		for (ActivityDateTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
