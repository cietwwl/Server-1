package com.playerdata.activity.exChangeType.cfg;



import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityExchangeTypeCfgDAO extends CfgCsvDao<ActivityExchangeTypeCfg> {
	public static ActivityExchangeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityExchangeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeCfg.csv", ActivityExchangeTypeCfg.class);
		for (ActivityExchangeTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		return cfgCacheMap;
	}


	public void parseTime(ActivityExchangeTypeCfg cfg){
		long dropStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropStartTimeStr());
		cfg.setDropStartTime(dropStartTime);
		long dropEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropEndTimeStr());
		cfg.setDropEndTime(dropEndTime);		
		long changeStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeStartTimeStr());
		cfg.setChangeStartTime(changeStartTime);
		long changeEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeEndTimeStr());
		cfg.setChangeEndTime(changeEndTime);		
	}		
	
	public ActivityExchangeTypeCfg getConfig(String id){
		ActivityExchangeTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	
}