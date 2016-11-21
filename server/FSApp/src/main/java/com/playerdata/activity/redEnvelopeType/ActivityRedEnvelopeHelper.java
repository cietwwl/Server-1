package com.playerdata.activity.redEnvelopeType;


public class ActivityRedEnvelopeHelper {
	public static String getItemId(String userId, ActivityRedEnvelopeTypeEnum typeEnum){
		
		return userId+"_"+typeEnum.getCfgId();
	}
}
