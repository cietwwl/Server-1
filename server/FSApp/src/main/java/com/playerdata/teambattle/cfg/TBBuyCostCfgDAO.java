package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TBBuyCostCfgDAO extends CfgCsvDao<TBBuyCostCfg> {
	public static TBBuyCostCfgDAO getInstance() {
		return SpringContextUtil.getBean(TBBuyCostCfgDAO.class);
	}

	@Override
	public Map<String, TBBuyCostCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("team/cost.csv",TBBuyCostCfg.class);
		Collection<TBBuyCostCfg> vals = cfgCacheMap.values();
		for (TBBuyCostCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
