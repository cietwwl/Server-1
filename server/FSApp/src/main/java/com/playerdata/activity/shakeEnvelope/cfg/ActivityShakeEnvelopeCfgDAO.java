package com.playerdata.activity.shakeEnvelope.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.shakeEnvelope.cfg.ActivityShakeEnvelopeCfgDAO"  init-method="init" />

public class ActivityShakeEnvelopeCfgDAO extends CfgCsvDao<ActivityShakeEnvelopeCfg> {
	public static ActivityShakeEnvelopeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityShakeEnvelopeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityShakeEnvelopeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityShakeEnvelopeCfg.csv",ActivityShakeEnvelopeCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
