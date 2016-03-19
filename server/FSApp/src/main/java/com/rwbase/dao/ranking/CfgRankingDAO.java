package com.rwbase.dao.ranking;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.ranking.pojo.CfgRanking;

public class CfgRankingDAO extends CfgCsvDao<CfgRanking> {
	private static CfgRankingDAO instance = new CfgRankingDAO();
	private CfgRankingDAO() {
		
	}
	
	public static CfgRankingDAO getInstance(){
		return instance;
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
