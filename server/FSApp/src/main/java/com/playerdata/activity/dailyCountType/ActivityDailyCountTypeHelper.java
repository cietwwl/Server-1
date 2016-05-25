package com.playerdata.activity.dailyCountType;

public class ActivityDailyCountTypeHelper {

	
	public static String getItemId(String userId, ActivityDailyCountTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
