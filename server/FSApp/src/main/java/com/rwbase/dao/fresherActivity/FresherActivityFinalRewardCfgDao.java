package com.rwbase.dao.fresherActivity;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityFinalRewardCfg;

public class FresherActivityFinalRewardCfgDao  extends CfgCsvDao<FresherActivityFinalRewardCfg>{
	
	public static FresherActivityFinalRewardCfgDao getInstance() {
		return SpringContextUtil.getBean(FresherActivityFinalRewardCfgDao.class);
	}
	
	@Override
	public Map<String, FresherActivityFinalRewardCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fresherActivity/FresherActivityFinalRewardCfg.csv", FresherActivityFinalRewardCfg.class);
		return cfgCacheMap;
	}

	public FresherActivityFinalRewardCfg getFresherActivityCfg(int cfgId){
		FresherActivityFinalRewardCfg cfg = (FresherActivityFinalRewardCfg)cfgCacheMap.get(String.valueOf(cfgId));
		return cfg;
	}
}
