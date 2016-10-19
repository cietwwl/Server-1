package com.playerdata.activity.rankType;

import java.util.HashMap;

public enum ActivityRankTypeEnum{	// implements TypeIdentification
	
	FIGHTING("70001",new int[]{203}),//战力大比拼
	ARENA("70002",new int[]{101,102,103,104});//竞技之王
	
	
	private String cfgId;
	private int[] rankTypes ;
	
	private ActivityRankTypeEnum(String cfgId,int[] rankTypes){
		this.cfgId = cfgId;
		this.rankTypes = rankTypes;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	

	public static HashMap<String, ActivityRankTypeEnum> getMap() {
		return map;
	}

	public static void setMap(HashMap<String, ActivityRankTypeEnum> map) {
		ActivityRankTypeEnum.map = map;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public void setRankTypes(int[] rankTypes) {
		this.rankTypes = rankTypes;
	}

	public int[] getRankTypes() {
		return rankTypes;
	}
	
	private static HashMap<String, ActivityRankTypeEnum> map;

	static {
		ActivityRankTypeEnum[] array = values();
		map = new HashMap<String, ActivityRankTypeEnum>();
		for (int i = 0; i < array.length; i++) {
			ActivityRankTypeEnum typeEnum = array[i];
			map.put(typeEnum.getCfgId(), typeEnum);
		}
	}

	public static ActivityRankTypeEnum getById(String cfgId) {
		return map.get(cfgId);
	}
	

	
	
	
}
