package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBExAwardCfgDAO extends CfgCsvDao<WBExAwardCfg> {	


	public static WBExAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBExAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBExAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBExAwardCfg.csv", WBExAwardCfg.class);
		return cfgCacheMap;
	}



}