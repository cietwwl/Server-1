package com.playerdata.activity.limitHeroType;

import com.playerdata.activity.fortuneCatType.ActivityFortuneTypeEnum;

public class ActivityLimitHeroHelper {
	public static String getItemId(String userId, ActivityLimitHeroEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
