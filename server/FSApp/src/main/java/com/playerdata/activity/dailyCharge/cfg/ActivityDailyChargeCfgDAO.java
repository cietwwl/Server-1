package com.playerdata.activity.dailyCharge.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO"  init-method="init" />

public class ActivityDailyChargeCfgDAO extends CfgCsvDao<ActivityDailyChargeCfg> {
	public static ActivityDailyChargeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyChargeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyChargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyChargeCfg.csv",ActivityDailyChargeCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
