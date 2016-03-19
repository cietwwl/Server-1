package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.GemCfg;

public class GemCfgDAO extends CfgCsvDao<GemCfg> {
	private static GemCfgDAO instance = new GemCfgDAO();
	private GemCfgDAO(){}
	public static GemCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, GemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Gem.csv",GemCfg.class);
		return cfgCacheMap;
	}
}
