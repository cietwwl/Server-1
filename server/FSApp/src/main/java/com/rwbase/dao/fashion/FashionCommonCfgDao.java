package com.rwbase.dao.fashion;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FashionCommonCfgDao extends CfgCsvDao<FashionCommonCfg> {
	public static FashionCommonCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionCommonCfgDao.class);
	}
	
	@Override
	public Map<String, FashionCommonCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionCommonCfg.csv", FashionCommonCfg.class);
		Collection<Entry<String,FashionCommonCfg>> values =  cfgCacheMap.entrySet();
		for (Entry<String,FashionCommonCfg> entry : values) {
			String key = entry.getKey();
			FashionCommonCfg cfg = entry.getValue();
			cfg.ExtraInit(key);
		}
		return cfgCacheMap;
	}
	
	public FashionCommonCfg getConfig(String id){
		FashionCommonCfg cfg = (FashionCommonCfg)getCfgById(id);
		return cfg;
	}
	
	public FashionCommonCfg getConfig(int fashionID){
		FashionCommonCfg cfg = (FashionCommonCfg)getCfgById(String.valueOf(fashionID));
		return cfg;
	}

}
