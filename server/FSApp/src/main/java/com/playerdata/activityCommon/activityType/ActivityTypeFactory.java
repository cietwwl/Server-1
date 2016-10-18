package com.playerdata.activityCommon.activityType;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activityCommon.ActivityType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActivityTypeFactory {
	
	public static final ActivityType DailyRecharge;
	private static List<ActivityType> typeList;
	
	static {
		DailyRecharge = new ActivityType(1001, ActivityDailyChargeCfgDAO.class, ActivityDailyRechargeTypeItem.class);
		
		typeList = new ArrayList<ActivityType>();
		typeList.add(DailyRecharge);
	}
	
	public List<ActivityType> getAllTypes(){
		return typeList;
	}
	
	synchronized static void addType(ActivityType type){
		List<ActivityType> newList = new ArrayList<ActivityType>(typeList);
		newList.add(type);
		typeList = newList;
	}
}
