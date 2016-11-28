package com.playerdata.activity.evilBaoArrive.data;

import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;


public class EvilBaoArriveItemHolder extends UserActivityChecker<EvilBaoArriveItem>{
	
	private static EvilBaoArriveItemHolder instance = new EvilBaoArriveItemHolder();
	
	public static EvilBaoArriveItemHolder getInstance(){
		return instance;
	}
	
	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityEvilBaoArrive;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.EvilBaoArrive;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_EVILBAOARRIVE;
	}
}
