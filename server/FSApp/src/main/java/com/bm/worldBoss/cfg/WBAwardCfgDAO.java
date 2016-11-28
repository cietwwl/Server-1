package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class WBAwardCfgDAO extends CfgCsvDao<WBAwardCfg> {	


	public static WBAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBAwardCfg.csv", WBAwardCfg.class);
		return cfgCacheMap;
	}



}