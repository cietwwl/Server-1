package com.playerdata.activity.consumeRank.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityConsumeRankItemHolder extends UserActivityChecker<ActivityConsumeRankItem>{
	
	private static ActivityConsumeRankItemHolder instance = new ActivityConsumeRankItemHolder();
	
	public static ActivityConsumeRankItemHolder getInstance(){
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ConsumeRank;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_CONSUME_RANK;
	}
	
	@Override
	protected eSynType getSynType() {
		//return eSynType.ActivityConsumeRank;
		return null;
	}
}
