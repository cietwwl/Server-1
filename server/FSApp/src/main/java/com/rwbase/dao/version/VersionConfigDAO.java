package com.rwbase.dao.version;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.userrole.AdvanceCfgDAO;
import com.rwbase.dao.version.pojo.VersionConfig;

public class VersionConfigDAO extends CfgCsvDao<VersionConfig> {

	public static VersionConfigDAO getInstance() {
		return SpringContextUtil.getBean(VersionConfigDAO.class);
	}
	@Override
	public Map<String, VersionConfig> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Version/VersionConfig.csv", VersionConfig.class);
		return cfgCacheMap;
	}

}
