package com.playerdata.activity.dailyCountType;

public class ActivityDailyTypeHelper {

	
	public static String getItemId(String userId, ActivityDailyTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
