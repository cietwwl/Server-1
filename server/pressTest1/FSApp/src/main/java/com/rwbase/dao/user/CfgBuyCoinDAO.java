package com.rwbase.dao.user;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgBuyCoinDAO extends CfgCsvDao<CfgBuyCoin> {
	private static CfgBuyCoinDAO instance = new CfgBuyCoinDAO();
	private CfgBuyCoinDAO() {
		
	}
	public static CfgBuyCoinDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, CfgBuyCoin> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("cfgBuyCoin/cfgbuycoin.csv",CfgBuyCoin.class);
		return cfgCacheMap;
	}

}
