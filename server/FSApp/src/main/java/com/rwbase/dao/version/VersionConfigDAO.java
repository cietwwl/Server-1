package com.rwbase.dao.version;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.version.pojo.VersionConfig;

public class VersionConfigDAO extends CfgCsvDao<VersionConfig> {

	private static VersionConfigDAO instance = new VersionConfigDAO();
	private VersionConfigDAO() {}
	public static VersionConfigDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, VersionConfig> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Version/VersionConfig.csv", VersionConfig.class);
		return cfgCacheMap;
	}

}
