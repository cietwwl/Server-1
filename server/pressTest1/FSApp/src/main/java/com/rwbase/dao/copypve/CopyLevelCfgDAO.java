package com.rwbase.dao.copypve;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copypve.pojo.CopyLevelCfg;

public class CopyLevelCfgDAO extends CfgCsvDao<CopyLevelCfg> {

	private static CopyLevelCfgDAO instance = new CopyLevelCfgDAO();
	private CopyLevelCfgDAO(){}
	public static CopyLevelCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, CopyLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("pve/copyLevel.csv", CopyLevelCfg.class);
		return cfgCacheMap;
	}

}
