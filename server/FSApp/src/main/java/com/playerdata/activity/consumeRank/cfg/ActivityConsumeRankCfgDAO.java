package com.playerdata.activity.consumeRank.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankCfgDAO"  init-method="init" />

public class ActivityConsumeRankCfgDAO extends CfgCsvDao<ActivityConsumeRankCfg> {
	public static ActivityConsumeRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityConsumeRankCfgDAO.class);
	}

	@Override
	public Map<String, ActivityConsumeRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityConsumeRank/ActivityChargeRankCfg.csv",ActivityConsumeRankCfg.class);
		Collection<ActivityConsumeRankCfg> vals = cfgCacheMap.values();
		for (ActivityConsumeRankCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
