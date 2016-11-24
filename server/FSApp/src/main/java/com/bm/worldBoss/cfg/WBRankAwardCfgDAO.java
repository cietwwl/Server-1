package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBRankAwardCfgDAO extends CfgCsvDao<WBRankAwardCfg> {	


	public static WBRankAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBRankAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBRankAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBRankAwardCfg.csv", WBRankAwardCfg.class);
		return cfgCacheMap;
	}


}