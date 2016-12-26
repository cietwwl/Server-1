package com.playerdata.activity.rankType;

import java.util.HashMap;

import com.bm.rank.RankType;

public enum ActivityRankTypeEnum{
	
	FIGHTING(70001, RankType.TEAM_FIGHTING),//战力大比拼
	ARENA(70002 , RankType.ARENA);//竞技之王
	
	private int cfgId;
	private RankType rankType;
	
	private ActivityRankTypeEnum(int cfgId, RankType rankType){
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

	public void setRankTypes(RankType rankType) {
		this.rankType = rankType;
	}

	public RankType getRankType() {
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
