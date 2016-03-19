package com.rwbase.dao.user;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgBuyPowerDAO extends CfgCsvDao<CfgBuyPower> {
	private static CfgBuyPowerDAO instance = new CfgBuyPowerDAO();
	private CfgBuyPowerDAO(){}
	public static CfgBuyPowerDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, CfgBuyPower> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("cfgbuypower/cfgbuypower.csv",CfgBuyPower.class);
		return cfgCacheMap;
	}

}
