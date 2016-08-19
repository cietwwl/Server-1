package com.playerdata.activity.redEnvelopeType;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.countType.ActivityCountTypeEnum;

public enum ActivityRedEnvelopeTypeEnum {
	redEnvelope("40001");
	
	private String cfgId;
	private ActivityRedEnvelopeTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}

	public static ActivityRedEnvelopeTypeEnum getById(String id) {
		ActivityRedEnvelopeTypeEnum target = null;
		for (ActivityRedEnvelopeTypeEnum enumTmp : values()) {
			if(StringUtils.equals(id, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}
	
}
