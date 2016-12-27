package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FortuneResultCfgDAO extends CfgCsvDao<FortuneResultCfg> {
	public static FortuneResultCfgDAO getInstance() {
		return SpringContextUtil.getBean(FortuneResultCfgDAO.class);
	}

	@Override
	public Map<String, FortuneResultCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/FortuneResult.csv", FortuneResultCfg.class);
		return cfgCacheMap;
	}
}
