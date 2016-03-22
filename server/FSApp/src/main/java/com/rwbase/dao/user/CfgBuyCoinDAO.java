package com.rwbase.dao.user;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.unendingwar.UnendingWarInfoCfgDAO;

public class CfgBuyCoinDAO extends CfgCsvDao<CfgBuyCoin> {
	public static CfgBuyCoinDAO getInstance() {
		return SpringContextUtil.getBean(CfgBuyCoinDAO.class);
	}
	@Override
	public Map<String, CfgBuyCoin> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("cfgBuyCoin/cfgbuycoin.csv",CfgBuyCoin.class);
		return cfgCacheMap;
	}

}
