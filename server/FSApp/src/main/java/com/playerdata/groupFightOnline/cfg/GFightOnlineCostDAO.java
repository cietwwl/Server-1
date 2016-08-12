package com.playerdata.groupFightOnline.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GFightOnlineCostDAO extends CfgCsvDao<GFightOnlineCostCfg> {
	public static GFightOnlineCostDAO getInstance() {
		return SpringContextUtil.getBean(GFightOnlineCostDAO.class);
	}

	@Override
	public Map<String, GFightOnlineCostCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupFightOnline/GFightOnlineCost.csv",GFightOnlineCostCfg.class);
		Collection<GFightOnlineCostCfg> vals = cfgCacheMap.values();
		for (GFightOnlineCostCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
