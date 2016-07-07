package com.playerdata.activity.redEnvelopeType;

public enum ActivityRedEnvelopeTypeEnum {
	redEnvelope("501");
	
	private String cfgId;
	private ActivityRedEnvelopeTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
}
