package com.playerdata.activity.timeCountType;

public class ActivityTimeCountTypeHelper {

	
	public static String getItemId(String userId, ActivityTimeCountTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
