package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;

public class FightingWeightCfgDAO  extends CfgCsvDao<FightingWeightCfg> {
	private static FightingWeightCfgDAO instance = new FightingWeightCfgDAO();
	private FightingWeightCfgDAO(){}
	public static FightingWeightCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, FightingWeightCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fighting/FightingWeightCfg.csv", FightingWeightCfg.class);
		return cfgCacheMap;
	}
}
