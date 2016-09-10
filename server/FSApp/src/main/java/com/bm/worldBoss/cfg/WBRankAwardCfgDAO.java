package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBRankAwardCfgDAO extends CfgCsvDao<WBCfg> {	


	public static WBRankAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBRankAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/wbCfg.csv", WBCfg.class);
		return cfgCacheMap;
	}


}