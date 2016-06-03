package com.playerdata.activity.VitalityType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class VitalityRewardCfgDAO extends CfgCsvDao<VitalityRewardCfg>{
	public static VitalityRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(VitalityRewardCfgDAO.class);
	}

	
	@Override
	public Map<String, VitalityRewardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeSubCfg.csv", VitalityRewardCfg.class);			
		return cfgCacheMap;
	}
	
	
	
}
