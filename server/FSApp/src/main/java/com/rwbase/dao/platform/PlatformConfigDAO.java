package com.rwbase.dao.platform;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.platform.pojo.PlatformConfig;

public class PlatformConfigDAO extends CfgCsvDao<PlatformConfig> {
	private static PlatformConfigDAO instance = new PlatformConfigDAO();
	private PlatformConfigDAO(){}
	public static PlatformConfigDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, PlatformConfig> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Platform/PlatformConfig.csv", PlatformConfig.class);
		return cfgCacheMap;
	}
}
