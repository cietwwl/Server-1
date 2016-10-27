package com.playerdata.activity.dailyCharge.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO"  init-method="init" />

public class ActivityDailyChargeSubCfgDAO extends CfgCsvDao<ActivityDailyChargeSubCfg> {
	public static ActivityDailyChargeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyChargeSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyChargeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyChargeSubCfg.csv",ActivityDailyChargeSubCfg.class);
		Collection<ActivityDailyChargeSubCfg> vals = cfgCacheMap.values();
		for (ActivityDailyChargeSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
