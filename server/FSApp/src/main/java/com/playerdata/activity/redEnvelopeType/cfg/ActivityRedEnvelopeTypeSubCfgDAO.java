package com.playerdata.activity.redEnvelopeType.cfg;


import java.util.Map;


import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityRedEnvelopeTypeSubCfgDAO extends CfgCsvDao<ActivityRedEnvelopeTypeSubCfg> {


	public static ActivityRedEnvelopeTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRedEnvelopeTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityRedEnvelopeTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRedEnvelopeTypeSubCfg.csv", ActivityRedEnvelopeTypeSubCfg.class);
		return cfgCacheMap;
	}
	
	


}