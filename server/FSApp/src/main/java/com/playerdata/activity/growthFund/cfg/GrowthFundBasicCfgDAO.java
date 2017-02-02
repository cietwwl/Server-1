package com.playerdata.activity.growthFund.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO"  init-method="init" />

public class GrowthFundBasicCfgDAO extends CfgCsvDao<GrowthFundBasicCfg> {
	
	public static GrowthFundBasicCfgDAO getInstance() {
		return SpringContextUtil.getBean(GrowthFundBasicCfgDAO.class);
	}

	@Override
	public Map<String, GrowthFundBasicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("growthFund/GrowthFundBasic.csv", GrowthFundBasicCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
