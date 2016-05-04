package com.playerdata.charge.cfg;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.countType.ActivityCountTypeEnum;

public enum ChargeTypeEnum {
	None,
	Normal,//普通充值
	MonthCard,//月卡
	VipMonthCard;//至尊月卡
	
	private String cfgId;

	
	public String getCfgId(){
		return cfgId;
	}
	
	public static ChargeTypeEnum getById(String cfgId){
		ChargeTypeEnum target = null;
		for (ChargeTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}
	
}
