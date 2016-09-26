package com.playerdata.activity.redEnvelopeType;



public enum ActivityRedEnvelopeTypeEnum {
	redEnvelope("40001");
	
	private String cfgId;
	private ActivityRedEnvelopeTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	public static ActivityRedEnvelopeTypeEnum getById(String cfgId) {
		if (redEnvelope.cfgId.equals(cfgId)) {
			return redEnvelope;
		} else {
			return null;
		}
	}
}
