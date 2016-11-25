package com.playerdata.activity.limitHeroType;


public class ActivityLimitHeroHelper {
	public static String getItemId(String userId, ActivityLimitHeroEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
