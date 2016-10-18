package com.playerdata.activityCommon.activityType;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activityCommon.ActivityType;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class RechargeRank{
	public static final ActivityType<ActivityDailyChargeCfgDAO, ActivityDailyRechargeTypeItem> type;
	static{
		type = new ActivityType(1001, ActivityDailyChargeCfgDAO.class, ActivityDailyRechargeTypeItem.class);
		ActivityTypeFactory.addType(type);
	}
}
