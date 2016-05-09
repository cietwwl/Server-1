package com.playerdata.activity.dateType;

public class ActivityDateTypeHelper {

	
	public static String getItemId(String userId, ActivityDateTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
