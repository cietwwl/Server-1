package com.playerdata.activity.dailyDiscountType;

public class ActivityDailyDiscountTypeHelper {

	
	public static String getItemId(String userId, ActivityDailyDiscountTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
