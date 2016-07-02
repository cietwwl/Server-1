package com.playerdata.activity.rankType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityRankTypeEnum{	// implements TypeIdentification
	
	FIGHTING("1001",new int[]{201}),//战力大比拼
	ARENA("1002",new int[]{101,102,103,104});//竞技之王
	
	
	private String cfgId;
	private int[] rankTypes ;
	
	private ActivityRankTypeEnum(String cfgId,int[] rankTypes){
		this.cfgId = cfgId;
		this.rankTypes = rankTypes;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityRankTypeEnum getById(String cfgId){
		ActivityRankTypeEnum target = null;
		for (ActivityRankTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	public int[] getRankTypes() {
		return rankTypes;
	}
	

	
	
	
}
