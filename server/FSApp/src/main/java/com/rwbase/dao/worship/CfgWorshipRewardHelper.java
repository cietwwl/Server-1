package com.rwbase.dao.worship;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.worship.pojo.CfgWorshipReward;

public class CfgWorshipRewardHelper extends CfgCsvDao<CfgWorshipReward> {
	public static CfgWorshipRewardHelper getInstance() {
		return SpringContextUtil.getBean(CfgWorshipRewardHelper.class);
	}
	
	public Map<String, CfgWorshipReward> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worship/worshipReward.csv",CfgWorshipReward.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public CfgWorshipReward getWorshipRewardCfg(String type){
		return (CfgWorshipReward)getCfgById(String.valueOf(type));
	}
}
