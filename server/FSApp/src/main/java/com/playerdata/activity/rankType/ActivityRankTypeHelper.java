package com.playerdata.activity.rankType;

public class ActivityRankTypeHelper {

	
	public static String getItemId(String userId, ActivityRankTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
