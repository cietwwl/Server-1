package com.playerdata.activityCommon;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;

public enum ActivityType1 {

	DailyRecharge(1001, ActivityDailyChargeCfgDAO.class,
			ActivityDailyRechargeTypeItem.class);

	private int typeId;
	private Class<? extends CfgCsvDao<? extends ActivityCfgIF>> value;
	private Class<?> activityItem;
	private volatile long verStamp = 0;

	ActivityType1(int id,
			Class<? extends CfgCsvDao<? extends ActivityCfgIF>> value,
			Class<?> activityItem) {
		this.typeId = id;
		this.value = value;
		this.activityItem = activityItem;
	}

	public CfgCsvDao<? extends ActivityCfgIF> getDao() {
		return SpringContextUtil.getBean(value);
	}

	public void addVerStamp() {
		verStamp++;
	}

	public long getVerStamp() {
		return verStamp;
	}

	public int getTypeId() {
		return typeId;
	}
}


