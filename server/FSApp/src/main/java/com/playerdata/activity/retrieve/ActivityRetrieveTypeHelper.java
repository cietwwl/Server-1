package com.playerdata.activity.retrieve;


public class ActivityRetrieveTypeHelper {
	
public static String getItemId(String userId, ActivityRetrieveTypeEnum typeEnum){		
		return userId+"_"+typeEnum.getId();
	}
	
}
