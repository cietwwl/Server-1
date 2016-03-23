package com.rwbase.dao.ranking;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.platform.PlatformConfigDAO;
import com.rwbase.dao.ranking.pojo.CfgRanking;

public class CfgRankingDAO extends CfgCsvDao<CfgRanking> {
	public static CfgRankingDAO getInstance() {
		return SpringContextUtil.getBean(CfgRankingDAO.class);
	}

	
	public Map<String, CfgRanking> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Ranking/ranking.csv",CfgRanking.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public CfgRanking getRankingCf(int rankType){
		return (CfgRanking)getCfgById(String.valueOf(rankType));
	}
}
