package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GiftCfgDAO extends CfgCsvDao<GiftCfg> {
	public static GiftCfgDAO getInstance() {
		return SpringContextUtil.getBean(GiftCfgDAO.class);
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
