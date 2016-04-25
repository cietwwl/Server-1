package com.playerdata.activity.countType;

public class ActivityCountTypeHelper {

	
	public static String getItemId(String userId, ActivityCountTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
