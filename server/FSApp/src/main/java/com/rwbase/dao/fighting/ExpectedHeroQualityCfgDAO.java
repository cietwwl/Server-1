package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.ExpectedHeroQualityCfg;

public class ExpectedHeroQualityCfgDAO extends CfgCsvDao<ExpectedHeroQualityCfg> {
	
	public static ExpectedHeroQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ExpectedHeroQualityCfgDAO.class);
	}

	@Override
	protected Map<String, ExpectedHeroQualityCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("SystemFightingExpected/ExpectedHeroQuality.csv", ExpectedHeroQualityCfg.class);
		return cfgCacheMap;
	}

}
