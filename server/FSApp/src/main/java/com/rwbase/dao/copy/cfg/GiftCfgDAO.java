package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class GiftCfgDAO extends CfgCsvDao<GiftCfg> {
	private static GiftCfgDAO instance = new GiftCfgDAO();
	
	public static GiftCfgDAO getInstance(){
		return instance;
	}
	
	public Map<String, GiftCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/gift.csv",GiftCfg.class);
		return cfgCacheMap;
	}
	
	public GiftCfg getCfg(int id){
		return getCfg(String.valueOf(id));
	}
	public GiftCfg getCfg(String id){
		return (GiftCfg)getCfgById(id);
	}
}
