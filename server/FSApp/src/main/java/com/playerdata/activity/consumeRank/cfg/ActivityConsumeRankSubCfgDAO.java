package com.playerdata.activity.consumeRank.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankSubCfgDAO"  init-method="init" />

public class ActivityConsumeRankSubCfgDAO extends CfgCsvDao<ActivityConsumeRankSubCfg> {
	public static ActivityConsumeRankSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityConsumeRankSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityConsumeRankSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityConsumeRank/ActivityChargeRankSubCfg.csv", ActivityConsumeRankSubCfg.class);
		Collection<ActivityConsumeRankSubCfg> vals = cfgCacheMap.values();
		for (ActivityConsumeRankSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
