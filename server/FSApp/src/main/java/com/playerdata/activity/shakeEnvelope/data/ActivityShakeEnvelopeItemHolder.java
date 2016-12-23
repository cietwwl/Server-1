package com.playerdata.activity.shakeEnvelope.data;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<ActivityShakeEnvelopeItem> getItemList(String userId){
		return refreshActivity(userId);
	}

	@Override
	public List<ActivityShakeEnvelopeSubItem> newSubItemList(String cfgId) {
		return new ArrayList<ActivityShakeEnvelopeSubItem>();
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
		//return eSynType.ActivityShakeEnvelope;
		return null;
	}
}
