package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBRankAwardCfgDAO extends CfgCsvDao<WBCfg> {	


	public static WBRankAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBRankAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/wbCfg.csv", WBCfg.class);
		for (WBCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		return cfgCacheMap;
	}


	public void parseTime(WBCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
		
	


}