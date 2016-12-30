package com.playerdata.activity.exChangeType.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityExchangeTypeItemHolder extends UserActivityChecker<ActivityExchangeTypeItem>{
	
	private static ActivityExchangeTypeItemHolder instance = new ActivityExchangeTypeItemHolder();
	
	public static ActivityExchangeTypeItemHolder getInstance(){
		return instance;
	}
	
	@Override
	public List<ActivityExchangeTypeSubItem> newSubItemList(String cfgId) {
		ActivityExchangeTypeCfg exchangeCfg = ActivityExchangeTypeCfgDAO.getInstance().getCfgById(cfgId);
		if(null == exchangeCfg){
			return Collections.emptyList();
		}
		List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
		List<String> todaySubs = getTodaySubActivity(cfgId);
		ActivityExchangeTypeSubCfgDAO subDao = ActivityExchangeTypeSubCfgDAO.getInstance();
		for(String subCfgId : todaySubs){
			ActivityExchangeTypeSubCfg subCfg = subDao.getCfgById(subCfgId);
			ActivityExchangeTypeSubItem subItem = new ActivityExchangeTypeSubItem();
			subItem.setCfgId(String.valueOf(subCfg.getId()));
			subItem.setTime(0);
			subItem.setIsrefresh(exchangeCfg.isDailyRefresh());
			subItemList.add(subItem);
		}
		return subItemList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ExChangeType;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_EXCHANGE;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityExchangeType;
	}
}
