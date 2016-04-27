package com.rw.service.gamble.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
/*
<bean class="com.rw.service.gamble.datamodel.GamblePlanCfgHelper"  init-method="init" />
*/

public class GamblePlanCfgHelper extends CfgCsvDao<GamblePlanCfg> {
	public static GamblePlanCfgHelper getInstance() {
		return SpringContextUtil.getBean(GamblePlanCfgHelper.class);
	}

	@Override
	public Map<String, GamblePlanCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/GamblePlanCfg.csv", GamblePlanCfg.class);
		Collection<GamblePlanCfg> vals = cfgCacheMap.values();
		for (GamblePlanCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}