package com.playerdata.activity.VitalityType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityVitalityRewardCfgDAO extends CfgCsvDao<ActivityVitalityRewardCfg>{
	
	public static ActivityVitalityRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalityRewardCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityVitalityRewardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityRewardCfg.csv", ActivityVitalityRewardCfg.class);			
		return cfgCacheMap;
	}
}
