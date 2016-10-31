package com.playerdata.activityCommon.activityType.newAddType;

import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActivitySample{
	public static final ActivityType<? extends CfgCsvDao<? extends ActivityCfgIF>, ? extends ActivityTypeItemIF> type;
	static{
		type = new ActivityType(1001, ActivityDailyChargeCfgDAO.class, ActivityDailyRechargeTypeItem.class, ActivityDailyRechargeTypeMgr.getInstance());
		//ActivityTypeFactory.addType(type);
	}
}
