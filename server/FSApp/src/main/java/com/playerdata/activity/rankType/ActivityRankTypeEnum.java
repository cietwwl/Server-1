package com.playerdata.activity.rankType;

import java.util.HashMap;

public enum ActivityRankTypeEnum{
	
	FIGHTING(70001, 203),//战力大比拼
	ARENA(70002 , 105);//竞技之王
	
	private int cfgId;
	private int rankType;
	
	private ActivityRankTypeEnum(int cfgId, int rankType){
		this.cfgId = cfgId;
		this.rankType = rankType;
	} 
	
	public int getCfgId(){
		return cfgId;
	}
	

	public static HashMap<Integer, ActivityRankTypeEnum> getMap() {
		return map;
	}

	public static void setMap(HashMap<Integer, ActivityRankTypeEnum> map) {
		ActivityRankTypeEnum.map = map;
	}

	public void setCfgId(Integer cfgId) {
		this.cfgId = cfgId;
	}

	public void setRankTypes(int rankType) {
		this.rankType = rankType;
	}

	public int getRankType() {
		return rankType;
	}
	
	private static HashMap<Integer, ActivityRankTypeEnum> map;

	static {
		ActivityRankTypeEnum[] array = values();
		map = new HashMap<Integer, ActivityRankTypeEnum>();
		for (int i = 0; i < array.length; i++) {
			ActivityRankTypeEnum typeEnum = array[i];
			map.put(typeEnum.getCfgId(), typeEnum);
		}
	}

	public static ActivityRankTypeEnum getById(Integer cfgId) {
		return map.get(cfgId);
	}
}
