package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.business.SevenDayGifCfgDAO;

public class BuyLevelCfgDAO extends CfgCsvDao<BuyLevelCfg> {
	public static BuyLevelCfgDAO getInstance() {
		return SpringContextUtil.getBean(BuyLevelCfgDAO.class);
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
