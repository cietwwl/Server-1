package com.playerdata.activity.chargeRank.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityChargeRankItemHolder extends UserActivityChecker<ActivityChargeRankItem>{

	private static ActivityChargeRankItemHolder instance = new ActivityChargeRankItemHolder();
	
	public static ActivityChargeRankItemHolder getInstance(){
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ChargeRank;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_CHARGE_RANK;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityChargeRank;
	}
}
