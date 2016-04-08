package com.playerdata.activity.countType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityCountTypeEnum {	
	Login("1");
	
	private String id;
	private ActivityCountTypeEnum(String id){
		this.id = id;
	} 
	
	public String getId(){
		return id;
	}
	
	public static ActivityCountTypeEnum getById(String activityId){
		ActivityCountTypeEnum target = null;
		
		for (ActivityCountTypeEnum enumTmp : values()) {
			if(StringUtils.equals(activityId, enumTmp.getId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}
	
	
}
