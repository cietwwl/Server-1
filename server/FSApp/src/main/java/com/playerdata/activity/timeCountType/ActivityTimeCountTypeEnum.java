package com.playerdata.activity.timeCountType;


public enum ActivityTimeCountTypeEnum{	// implements TypeIdentification
	role_online("30001");
	
	private String cfgId;
	private ActivityTimeCountTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	

	public static ActivityTimeCountTypeEnum getById(String cfgId) {
		if (role_online.cfgId.equals(cfgId)) {
			return role_online;
		} else {
			return null;
		}
	}
}
