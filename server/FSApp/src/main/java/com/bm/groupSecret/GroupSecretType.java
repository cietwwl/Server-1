package com.bm.groupSecret;

import org.apache.commons.lang3.StringUtils;

public enum GroupSecretType {
	
	Gold("1");
	
	private String cfgId;
	
	private GroupSecretType(String  cfgId){
		this.cfgId = cfgId;
	}
	
	public String getCfgId(){
		return cfgId;
	}	
	
	public static GroupSecretType valueOf(int ordinal){
		GroupSecretType target = null;
		for (GroupSecretType enumTmp : values()) {
			if(enumTmp.ordinal() == ordinal){
				target = enumTmp;
				break;
			}
		}	
		return target;
	}
	
	public static GroupSecretType getById(String cfgId){
		GroupSecretType target = null;
		for (GroupSecretType enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

}
