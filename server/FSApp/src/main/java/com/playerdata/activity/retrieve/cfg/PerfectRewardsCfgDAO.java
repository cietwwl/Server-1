package com.playerdata.activity.retrieve.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class PerfectRewardsCfgDAO extends CfgCsvDao<PerfectRewardsCfg>{

	@Override
	protected Map<String, PerfectRewardsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/NorRewardsList.csv", PerfectRewardsCfg.class);
		for (PerfectRewardsCfg cfgTmp : cfgCacheMap.values()) {		
			parse(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parse(PerfectRewardsCfg cfgTmp) {
		// TODO Auto-generated method stub
		
	}

}
