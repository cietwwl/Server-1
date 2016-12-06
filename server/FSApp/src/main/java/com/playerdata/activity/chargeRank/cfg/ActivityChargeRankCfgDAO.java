package com.playerdata.activity.chargeRank.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.chargeRank.cfg.ActivityChargeRankCfgDAO"  init-method="init" />

public class ActivityChargeRankCfgDAO extends CfgCsvDao<ActivityChargeRankCfg> {
	public static ActivityChargeRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityChargeRankCfgDAO.class);
	}

	@Override
	public Map<String, ActivityChargeRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityChargeRank/ActivityChargeRankCfg.csv",ActivityChargeRankCfg.class);
		Collection<ActivityChargeRankCfg> vals = cfgCacheMap.values();
		for (ActivityChargeRankCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
