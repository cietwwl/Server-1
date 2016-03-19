package com.rwbase.dao.tower;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerFightLimitDAO extends CfgCsvDao<TowerFightLimitCfg> {
	private static TowerFightLimitDAO instance  =  new TowerFightLimitDAO();
	private TowerFightLimitDAO(){};
	public static TowerFightLimitDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, TowerFightLimitCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerFightLimit.csv",TowerFightLimitCfg.class);
		return cfgCacheMap;
	}
	public TowerFightLimitCfg getCfgByTowerId(int towerId){
		TowerFightLimitCfg limitCfg = (TowerFightLimitCfg)super.getCfgById(String.valueOf((towerId+1)));
        return limitCfg;
	}
}
