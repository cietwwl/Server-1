package com.playerdata.activity.retrieve.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class NormalRewardsCfgDAO extends CfgCsvDao<NormalRewardsCfg>{

	@Override
	protected Map<String, NormalRewardsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/NorRewardsList.csv", NormalRewardsCfg.class);
		for (NormalRewardsCfg cfgTmp : cfgCacheMap.values()) {		
			parse(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parse(NormalRewardsCfg cfgTmp) {
		// TODO Auto-generated method stub
		
	}

}
