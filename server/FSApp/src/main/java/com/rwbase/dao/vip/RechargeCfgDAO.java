package com.rwbase.dao.vip;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.vip.pojo.RechargeCfg;

public class RechargeCfgDAO extends CfgCsvDao<RechargeCfg> {
	public static RechargeCfgDAO getInstance() {
		return SpringContextUtil.getBean(RechargeCfgDAO.class);
	}
	@Override
	public Map<String, RechargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("vip/RechargeCfg.csv",RechargeCfg.class);
		return cfgCacheMap;
	}
}
