package com.rwbase.dao.worship;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.worship.pojo.CfgWorshipRankdomScheme;

public class CfgWorshipRankdomSchemeHelper extends CfgCsvDao<CfgWorshipRankdomScheme>{
	private static CfgWorshipRankdomSchemeHelper instance = new CfgWorshipRankdomSchemeHelper();
	private CfgWorshipRankdomSchemeHelper() {
		
	}
	
	public static CfgWorshipRankdomSchemeHelper getInstance(){
		return instance;
	}
	
	public Map<String, CfgWorshipRankdomScheme> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worship/worshipRandomScheme.csv",CfgWorshipRankdomScheme.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public CfgWorshipRankdomScheme getWorshipRewardCfg(int type){
		return (CfgWorshipRankdomScheme)getCfgById(String.valueOf(type));
	}
}
