package com.playerdata.activity.shakeEnvelope.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityShakeEnvelopeItemHolder extends UserActivityChecker<ActivityShakeEnvelopeItem>{

	private static ActivityShakeEnvelopeItemHolder instance = new ActivityShakeEnvelopeItemHolder();
	
	public static ActivityShakeEnvelopeItemHolder getInstance(){
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ShakeEnvelope;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_SHAKE_ENVELOPE;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityChargeRank;
	}
}
