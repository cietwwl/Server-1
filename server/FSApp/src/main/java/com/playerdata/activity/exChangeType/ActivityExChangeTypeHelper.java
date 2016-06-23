package com.playerdata.activity.exChangeType;

public class ActivityExChangeTypeHelper {

	
	public static String getItemId(String userId, ActivityExChangeTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
