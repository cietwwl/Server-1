package com.playerdata.activity.dailyCountType.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyTypeItemHolder extends UserActivityChecker<ActivityDailyTypeItem>{

	private static ActivityDailyTypeItemHolder instance = new ActivityDailyTypeItemHolder();

	public static ActivityDailyTypeItemHolder getInstance() {
		return instance;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return null;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_DAILYTYPE;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityDailyType;
	}
}
