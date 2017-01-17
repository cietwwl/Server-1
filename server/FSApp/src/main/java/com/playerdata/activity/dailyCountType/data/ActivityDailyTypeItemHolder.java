package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyTypeItemHolder extends UserActivityChecker<ActivityDailyTypeItem>{

	private static ActivityDailyTypeItemHolder instance = new ActivityDailyTypeItemHolder();

	public static ActivityDailyTypeItemHolder getInstance() {
		return instance;
	}
	
	@Override
	public List<ActivityDailyTypeSubItem> newSubItemList(String cfgId) {
		List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
		List<String> todaySubs = getTodaySubActivity(cfgId);
		ActivityDailyTypeSubCfgDAO subDao = ActivityDailyTypeSubCfgDAO.getInstance();
		for(String subCfgId : todaySubs){
			ActivityDailyTypeSubCfg subCfg = subDao.getCfgById(subCfgId);
			ActivityDailyTypeSubItem item = new ActivityDailyTypeSubItem();
			item.setCfgId(String.valueOf(subCfg.getId()));
			item.setCount(0);
			item.setTaken(false);
			item.setGiftId(subCfg.getGiftId());
			subItemList.add(item);
		}
		return subItemList;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyCount;
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
