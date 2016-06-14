package com.rwbase.dao.copypve;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copypve.pojo.CopyLevelCfg;

public class CopyLevelCfgDAO extends CfgCsvDao<CopyLevelCfg> {

	public static CopyLevelCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyLevelCfgDAO.class);
	}
	
	@Override
	public Map<String, CopyLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("pve/copyLevel.csv", CopyLevelCfg.class);
		return cfgCacheMap;
	}

}
