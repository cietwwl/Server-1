package com.rwbase.dao.fashion;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class FashionCfgDao extends CfgCsvDao<FashionCfg> {
	private static FashionCfgDao instance = new FashionCfgDao();
	private FashionCfgDao(){}
	public static FashionCfgDao getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, FashionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionCfg.csv", FashionCfg.class);
		return cfgCacheMap;
	}
	
	public FashionCfg getConfig(String id){
		FashionCfg cfg = (FashionCfg)getCfgById(id);
		return cfg;
	}
	
	public FashionCfg getConfig(int suitId,int career,int sex){
		List<FashionCfg> all = super.getAllCfg();
		for (FashionCfg cfg : all) {
			if(cfg.getSuitId() == suitId && cfg.getCareer() == career && cfg.getSex() == sex){
				return cfg;
			}
		}
		return null;
	}
	
}
