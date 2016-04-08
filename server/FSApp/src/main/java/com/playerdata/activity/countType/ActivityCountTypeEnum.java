package com.playerdata.activity.countType;

public enum ActivityCountTypeEnum {	
	Login("1");
	
	private String id;
	private ActivityCountTypeEnum(String id){
		this.id = id;
	} 
	
	public String getId(){
		return id;
	}
}
