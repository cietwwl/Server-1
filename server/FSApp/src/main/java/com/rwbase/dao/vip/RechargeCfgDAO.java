package com.rwbase.dao.vip;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.vip.pojo.RechargeCfg;

public class RechargeCfgDAO extends CfgCsvDao<RechargeCfg> {
	private static RechargeCfgDAO instance = new RechargeCfgDAO();
	private RechargeCfgDAO(){
		
	}
	public static RechargeCfgDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, RechargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("vip/RechargeCfg.csv",RechargeCfg.class);
		return cfgCacheMap;
	}
}
