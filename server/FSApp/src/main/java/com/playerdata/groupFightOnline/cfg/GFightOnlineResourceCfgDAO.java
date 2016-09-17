package com.playerdata.groupFightOnline.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public class GFightOnlineResourceCfgDAO extends CfgCsvDao<GFightOnlineResourceCfg> {
	public static GFightOnlineResourceCfgDAO getInstance() {
		return SpringContextUtil.getBean(GFightOnlineResourceCfgDAO.class);
	}

	@Override
	public Map<String, GFightOnlineResourceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupFightOnline/GFightOnlineResource.csv",GFightOnlineResourceCfg.class);
		Collection<GFightOnlineResourceCfg> vals = cfgCacheMap.values();
		for (GFightOnlineResourceCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
