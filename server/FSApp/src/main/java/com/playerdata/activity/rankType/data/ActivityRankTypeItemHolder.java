package com.playerdata.activity.rankType.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityRankTypeItemHolder extends UserActivityChecker<ActivityRankTypeItem>{
	
	private static ActivityRankTypeItemHolder instance = new ActivityRankTypeItemHolder();
	
	public static ActivityRankTypeItemHolder getInstance(){
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ActRankType;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_RANK;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityRankType;
	}
}
