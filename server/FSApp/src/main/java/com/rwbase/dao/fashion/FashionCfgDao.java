package com.rwbase.dao.fashion;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.email.EmailCfgDAO;

public class FashionCfgDao extends CfgCsvDao<FashionCfg> {
	public static FashionCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionCfgDao.class);
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
	
	public FashionCfg getConfig(int id){
		return getConfig(String.valueOf(id));
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
