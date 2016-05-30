package com.playerdata.activity.timeCountType;

public class ActivityTimeCountTypeHelper {

	//超过这个时间的计时不算
	final public static int FailCountTimeSpanInSecond = 80;
	
	public static String getItemId(String userId, ActivityTimeCountTypeEnum countTypeEnum){
		
		return userId+"_"+countTypeEnum.getCfgId();
	}
}
