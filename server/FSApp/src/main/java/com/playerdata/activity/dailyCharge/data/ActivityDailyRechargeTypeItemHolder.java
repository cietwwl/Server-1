package com.playerdata.activity.dailyCharge.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityDailyRechargeTypeItemHolder extends UserActivityChecker<ActivityDailyRechargeTypeItem>{
	
	private static ActivityDailyRechargeTypeItemHolder instance = new ActivityDailyRechargeTypeItemHolder();
	
	public static ActivityDailyRechargeTypeItemHolder getInstance(){
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyRecharge;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_DAILYCHARGE;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityDailyRechargeType;
	}
}
