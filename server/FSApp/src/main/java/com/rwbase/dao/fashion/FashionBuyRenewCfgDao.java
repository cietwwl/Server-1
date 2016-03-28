package com.rwbase.dao.fashion;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FashionBuyRenewCfgDao extends CfgCsvDao<FashionBuyRenewCfg> {
	public static FashionBuyRenewCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionBuyRenewCfgDao.class);
	}

	
	@Override
	public Map<String, FashionBuyRenewCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionBuyRenewCfg.csv", FashionBuyRenewCfg.class);
		Collection<FashionBuyRenewCfg> values =  cfgCacheMap.values();
		for (FashionBuyRenewCfg cfg : values) {
			cfg.ExtraInit();
		}
		return cfgCacheMap;
	}
	
	public FashionBuyRenewCfg getConfig(String id){
		FashionBuyRenewCfg cfg = (FashionBuyRenewCfg)getCfgById(id);
		return cfg;
	}
	
	public FashionBuyRenewCfg getConfig(int fashionID){
		FashionBuyRenewCfg cfg = (FashionBuyRenewCfg)getCfgById(String.valueOf(fashionID));
		return cfg;
	}
}
