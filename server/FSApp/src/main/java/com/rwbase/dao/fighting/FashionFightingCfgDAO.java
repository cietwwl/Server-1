package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.FashionFightingCfg;

public class FashionFightingCfgDAO extends CfgCsvDao<FashionFightingCfg> {

	public static final FashionFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FashionFightingCfgDAO.class);
	}

	@Override
	protected Map<String, FashionFightingCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("SystemFightingExpected/FashionFighting.csv", FashionFightingCfg.class);
		return this.cfgCacheMap;
	}
	
	

}
