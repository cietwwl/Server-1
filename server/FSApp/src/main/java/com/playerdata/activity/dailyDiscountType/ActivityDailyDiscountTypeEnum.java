package com.playerdata.activity.dailyDiscountType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityDailyDiscountTypeEnum{

	DailyDiscount("80001");//超值欢乐购	
	
	private String cfgId;
	private ActivityDailyDiscountTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}	
	
	public static ActivityDailyDiscountTypeEnum getById(String cfgId){
		ActivityDailyDiscountTypeEnum target = null;
		for (ActivityDailyDiscountTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}		
		return target;
	}
	
}
