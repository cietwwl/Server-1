package com.playerdata.activity.retrieve.userFeatures;

import java.util.HashMap;

import com.playerdata.activity.rankType.ActivityRankTypeEnum;

public enum UserFeaturesEnum {

	breakfast(1);
	
	private int id;
	
	private UserFeaturesEnum(int id){
		this.id = id;
	}
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static HashMap<Integer, UserFeaturesEnum> getMap() {
		return map;
	}

	public static void setMap(HashMap<Integer, UserFeaturesEnum> map) {
		UserFeaturesEnum.map = map;
	}


	private static HashMap<Integer, UserFeaturesEnum> map;
	
	static {
		UserFeaturesEnum[] array = values();
		map = new HashMap<Integer, UserFeaturesEnum>();
		for(int i = 0;i < array.length; i++){
			UserFeaturesEnum featuresEnum = array[i];
			map.put(featuresEnum.getId(), featuresEnum);
		}		
	}
	
	public static UserFeaturesEnum getById(int cfgId) {
	return map.get(cfgId);
}
	
	
}
