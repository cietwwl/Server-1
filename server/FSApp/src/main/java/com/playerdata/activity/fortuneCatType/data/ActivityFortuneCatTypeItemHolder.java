package com.playerdata.activity.fortuneCatType.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfgDAO;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityFortuneCatTypeItemHolder extends UserActivityChecker<ActivityFortuneCatTypeItem>{
	
	private static ActivityFortuneCatTypeItemHolder instance = new ActivityFortuneCatTypeItemHolder();
	
	public static ActivityFortuneCatTypeItemHolder getInstance(){
		return instance;
	}
	
	@Override
	public List<ActivityFortuneCatTypeSubItem> newSubItemList(String cfgId){
		List<ActivityFortuneCatTypeSubItem> subItemList = new ArrayList<ActivityFortuneCatTypeSubItem>();
		List<ActivityFortuneCatTypeSubCfg> subCfgList = ActivityFortuneCatTypeSubCfgDAO.getInstance().getCfgListByParentId(cfgId);
		if(subCfgList == null){
			return subItemList;
		}
		for(ActivityFortuneCatTypeSubCfg subCfg : subCfgList){
			ActivityFortuneCatTypeSubItem item = new ActivityFortuneCatTypeSubItem();
			item.setCfgId(subCfg.getId()+"");
			item.setNum(subCfg.getNum());
			item.setCost(subCfg.getCost()+"");
			item.setVip(subCfg.getVip());
			item.setGetGold(0);
			subItemList.add(item);
		}
		return subItemList;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.FortuneCat;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_FORTUNECAT;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityFortuneCatType;
	}	
}
