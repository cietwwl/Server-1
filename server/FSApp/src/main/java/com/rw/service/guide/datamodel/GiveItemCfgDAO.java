package com.rw.service.guide.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GiveItemCfgDAO extends CfgCsvDao<GiveItemCfg> {
	public static GiveItemCfgDAO getInstance() {
		return SpringContextUtil.getBean(GiveItemCfgDAO.class);
	}

	@Override
	public Map<String, GiveItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guidance/GiveItemCfg.csv",GiveItemCfg.class);
		Collection<GiveItemCfg> vals = cfgCacheMap.values();
		for (GiveItemCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}