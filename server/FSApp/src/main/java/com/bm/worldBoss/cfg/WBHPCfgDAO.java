package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBHPCfgDAO extends CfgCsvDao<WBHPCfg> {	


	public static WBHPCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBHPCfgDAO.class);
	}

	@Override
	public Map<String, WBHPCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBHPCfg.csv", WBHPCfg.class);
		return cfgCacheMap;
	}



}