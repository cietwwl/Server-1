package com.playerdata.activity.timeCardType;

public class ActivityTimeCardTypeHelper {

	
	public static String getItemId(String userId, ActivityTimeCardTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
