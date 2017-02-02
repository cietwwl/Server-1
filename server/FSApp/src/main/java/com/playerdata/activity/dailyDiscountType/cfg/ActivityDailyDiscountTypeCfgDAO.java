package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityDailyDiscountTypeCfgDAO extends CfgCsvDao<ActivityDailyDiscountTypeCfg> {

	public static ActivityDailyDiscountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountTypeCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityDailyDiscountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyDiscountTypeCfg.csv", ActivityDailyDiscountTypeCfg.class);
		return cfgCacheMap;
	}
}