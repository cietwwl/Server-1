package com.playerdata.activity.chargeRank.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.chargeRank.cfg.ActivityChargeRankSubCfgDAO"  init-method="init" />

public class ActivityChargeRankSubCfgDAO extends CfgCsvDao<ActivityChargeRankSubCfg> {
	public static ActivityChargeRankSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityChargeRankSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityChargeRankSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityChargeRank/ActivityChargeRankSubCfg.csv",ActivityChargeRankSubCfg.class);
		Collection<ActivityChargeRankSubCfg> vals = cfgCacheMap.values();
		for (ActivityChargeRankSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
