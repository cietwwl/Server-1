package com.playerdata.activity.countType.data;

import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityCountTypeItemHolder extends UserActivityChecker<ActivityCountTypeItem>{
	
	private static ActivityCountTypeItemHolder instance = new ActivityCountTypeItemHolder();

	public static ActivityCountTypeItemHolder getInstance() {
		return instance;
	}

	public ActivityCountTypeItem getItem(String userId, ActivityCountTypeEnum countTypeEnum){		
		int id = Integer.parseInt(countTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.CountType;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_COUNTTYPE;
	}
	
	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityCountType;
	}
}
