package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author Allen
 */
public final class WBBuyBuffCfgDAO extends CfgCsvDao<WBBuyBuffCfg> {	


	public static WBBuyBuffCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBBuyBuffCfgDAO.class);
	}

	
	@Override
	public Map<String, WBBuyBuffCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBBuyBuffCfg.csv", WBBuyBuffCfg.class);
			
		return cfgCacheMap;
	}
	



	


}