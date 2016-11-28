package com.playerdata.activity.dailyDiscountType.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfgDao;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfgDAO;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyDiscountTypeItemHolder extends UserActivityChecker<ActivityDailyDiscountTypeItem>{
	
	private static ActivityDailyDiscountTypeItemHolder instance = new ActivityDailyDiscountTypeItemHolder();
	
	public static ActivityDailyDiscountTypeItemHolder getInstance(){
		return instance;
	}
	
	@Override
	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(String cfgId){
		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
		List<String> todaySubs = getTodaySubActivity(cfgId);
		ActivityDailyDiscountTypeSubCfgDAO subDao = ActivityDailyDiscountTypeSubCfgDAO.getInstance();
		ActivityDailyDiscountItemCfgDao activityDailyDiscountItemCfgDao = ActivityDailyDiscountItemCfgDao.getInstance();
		for(String subCfgId : todaySubs){
			ActivityDailyDiscountTypeSubCfg subCfg = subDao.getCfgById(subCfgId);
			for(Integer itemId: subCfg.getItemList()){
				ActivityDailyDiscountItemCfg itemCfg = activityDailyDiscountItemCfgDao.getCfgById(String.valueOf(itemId));
				if(itemCfg == null){
					continue;
				}
				ActivityDailyDiscountTypeSubItem subitem = new ActivityDailyDiscountTypeSubItem();
				subitem.setCfgId(itemCfg.getId());
				subitem.setItemId(itemCfg.getItemId());
				subitem.setItemNum(itemCfg.getItemNum());
				subitem.setCount(0);
				subItemList.add(subitem);
			}
		}
		return subItemList;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyDiscount;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_DAILYDISCOUNT;
	}
	
	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityDailyDiscountType;
	}
}
