package com.rwbase.common.attribute.param;

public class MagicEquipFetterParam {

	//角色id
	private String userID;
	//英雄id
	private int heroModelID;
	
	public MagicEquipFetterParam(String userID, int heroID) {
		super();
		this.userID = userID;
		this.heroModelID = heroID;
	}
	public String getUserID() {
		return userID;
	}
	public int getHeroModelID() {
		return heroModelID;
	}
	
	
	
	
	
}
