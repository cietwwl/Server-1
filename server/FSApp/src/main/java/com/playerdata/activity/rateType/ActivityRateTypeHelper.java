package com.playerdata.activity.rateType;

public class ActivityRateTypeHelper {

	
	public static String getItemId(String userId, ActivityRateTypeEnum typeEnum){
		
		return userId+"_"+typeEnum.getCfgId();
	}
}
