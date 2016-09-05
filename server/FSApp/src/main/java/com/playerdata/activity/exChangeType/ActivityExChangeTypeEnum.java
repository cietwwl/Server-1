package com.playerdata.activity.exChangeType;

import java.util.HashMap;

public enum ActivityExChangeTypeEnum{	// implements TypeIdentification	
	ExChangeActive("60001"),
	MidAutumnFestival("60002"),
	DragonBoatFestival("60003");
	
	
	
	private String cfgId;
	private ActivityExChangeTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	private static HashMap<String, ActivityExChangeTypeEnum> map;

	static {
		ActivityExChangeTypeEnum[] array = values();
		map = new HashMap<String, ActivityExChangeTypeEnum>();
		for (int i = 0; i < array.length; i++) {
			ActivityExChangeTypeEnum typeEnum = array[i];
			map.put(typeEnum.getCfgId(), typeEnum);
		}
	}

	public static ActivityExChangeTypeEnum getById(String cfgId) {
		return map.get(cfgId);
	}
	
}
