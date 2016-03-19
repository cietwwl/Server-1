package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class BuyLevelCfgDAO extends CfgCsvDao<BuyLevelCfg> {
	private static BuyLevelCfgDAO instance = new BuyLevelCfgDAO();
	private BuyLevelCfgDAO() {}
	public static BuyLevelCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, BuyLevelCfg> initJsonCfg() {

		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/cfgbuylevel.csv",BuyLevelCfg.class);
		return cfgCacheMap;
	}

//	public static void main(String[] args) {
//		Map<String, Object> levels = CfgBuyLevelDAO.getInstance().initJsonCfg();
//		for (Object object : levels.values()) {
//			BuyLevelCfg cfgBuyLevel = (BuyLevelCfg)object;
//			System.out.println(cfgBuyLevel.getNeedPurse());
//		}
//		CfgBuyLevelDAO.getInstance().initJsonCfg();
//	}
}
