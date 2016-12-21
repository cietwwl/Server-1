package com.playerdata.activity.shakeEnvelope.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.chargeRank.cfg.ActivityChargeRankSubCfgDAO"  init-method="init" />

public class ActivityShakeEnvelopeSubCfgDAO extends CfgCsvDao<ActivityShakeEnvelopeSubCfg> {
	public static ActivityShakeEnvelopeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityShakeEnvelopeSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityShakeEnvelopeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityChargeRank/ActivityChargeRankSubCfg.csv",ActivityShakeEnvelopeSubCfg.class);
		Collection<ActivityShakeEnvelopeSubCfg> vals = cfgCacheMap.values();
		for (ActivityShakeEnvelopeSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
