package com.playerdata.activity.fortuneCatType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityFortuneCatTypeCfgDAO extends CfgCsvDao<ActivityFortuneCatTypeCfg> {

	public static ActivityFortuneCatTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityFortuneCatTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityFortuneCatTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityFortunCatTypeCfg.csv", ActivityFortuneCatTypeCfg.class);
		return cfgCacheMap;
	}
}