package com.rw.routerServer.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class UCGiftCfgDAO extends CfgCsvDao<UCGiftCfg>{
	
	public static UCGiftCfgDAO getInstance(){
		return SpringContextUtil.getBean(UCGiftCfgDAO.class);
	}

	@Override
	protected Map<String, UCGiftCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("ucGift/UCGiftCfg.csv", UCGiftCfg.class);
		return cfgCacheMap;
	}

}
