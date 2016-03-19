package com.rwbase.dao.gulid.faction;

public enum GuildPositionType {

	MASTER, //帮主
	SLAVE_MASTER, //副帮主
	OFFICER,//官员
	MEMBER;//成员
	
	public static GuildPositionType valueOf(int ordinal){
		if(ordinal<0 || ordinal>= values().length){
			return null;
		}else{
			return values()[ordinal];
		}
	}

	
}
