package com.rwbase.dao.store;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.pojo.StoreCfg;

public class StoreCfgDAO extends CfgCsvDao<StoreCfg> {
	public static StoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(StoreCfgDAO.class);
	}
	@Override
	public Map<String, StoreCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("store/StoreCfg.csv",StoreCfg.class);
		return cfgCacheMap;
	}
	
	public StoreCfg getStoreCfg(int type){
		List<StoreCfg> allcfg = super.getAllCfg();
		for (StoreCfg cfg : allcfg) {
			if(cfg.getType() == type){
				return cfg;
			}
		}
		return null;
	}
	public StoreCfg getStoreCfgByID(int id){
		List<StoreCfg> allcfg = super.getAllCfg();
		for (StoreCfg cfg : allcfg) {
			if(cfg.getId() == id){
				return cfg;
			}
		}
		return null;
	}

}
