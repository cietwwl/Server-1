package com.playerdata.activity.growthFund.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.growthFund.datamodel.GrowthFundGiftHelper"  init-method="init" />

public class GrowthFundSubCfgDAO extends CfgCsvDao<ActivitySubCfgIF> {
	public static GrowthFundSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(GrowthFundSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivitySubCfgIF> initJsonCfg() {
		Map<String, GrowthFundGiftCfg> giftCfgMap = CfgCsvHelper.readCsv2Map("growthFund/GrowthFundGift.csv",GrowthFundGiftCfg.class);
		Map<String, GrowthFundRewardCfg> rewardCfgMap = CfgCsvHelper.readCsv2Map("growthFund/GrowthFundGift.csv",GrowthFundRewardCfg.class);
		
		if(null != giftCfgMap){
			for(GrowthFundGiftCfg cfg : giftCfgMap.values()){
				cfgCacheMap.put(String.valueOf(cfg.getId()), cfg);
			}
		}
		
		if(null != rewardCfgMap){
			for(GrowthFundRewardCfg cfg : rewardCfgMap.values()){
				cfgCacheMap.put(String.valueOf(cfg.getId()), cfg);
			}
		}

		return cfgCacheMap;
	}
}
