package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author Allen
 */
public final class WBTaskCfgDAO extends CfgCsvDao<WBTaskCfg> {	


	public static WBTaskCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBTaskCfgDAO.class);
	}

	
	@Override
	public Map<String, WBTaskCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBTaskCfg.csv", WBTaskCfg.class);
			
		return cfgCacheMap;
	}
	



	


}