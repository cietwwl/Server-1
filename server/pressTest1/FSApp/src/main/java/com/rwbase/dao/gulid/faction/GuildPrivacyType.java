package com.rwbase.dao.gulid.faction;

public enum GuildPrivacyType {

	PUBLIC,//公开，所有人可加入
	NEED_CONFIRM,//需要验证才能加入
	PRIVATE;//只有自己，别人不能加入
	
	
	public static GuildPrivacyType valueOf(int ordinal){
		if(ordinal<0 || ordinal>= values().length){
			return null;
		}else{
			return values()[ordinal];
		}
	}
	
}
