package com.playerdata.activity.VitalityType.cfg;


import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class VitalityCfgDAO extends CfgCsvDao<VitalityCfg> {


	


	public static VitalityCfgDAO getInstance() {
		return SpringContextUtil.getBean(VitalityCfgDAO.class);
	}

	
	@Override
	public Map<String, VitalityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityCountTypeCfg.csv", VitalityCfg.class);
		for (VitalityCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	



	public void parseTime(VitalityCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);		
	}
		
	
	public VitalityCfg getConfig(String id){
		VitalityCfg cfg = getCfgById(id);
		return cfg;
	}


	


}