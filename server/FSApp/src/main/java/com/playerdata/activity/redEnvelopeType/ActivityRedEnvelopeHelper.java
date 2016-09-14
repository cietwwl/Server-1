package com.playerdata.activity.redEnvelopeType;

import com.playerdata.activity.rateType.ActivityRateTypeEnum;

public class ActivityRedEnvelopeHelper {
	public static String getItemId(String userId, ActivityRedEnvelopeTypeEnum typeEnum){
		
		return userId+"_"+typeEnum.getCfgId();
	}
}
