package com.playerdata.activity.VitalityType;

public class ActivityVitalityTypeHelper {

	
	public static String getItemId(String userId, ActivityVitalityTypeEnum acVitalityTypeEnum){
		
		return userId+"_"+acVitalityTypeEnum.getCfgId();
	}
}
