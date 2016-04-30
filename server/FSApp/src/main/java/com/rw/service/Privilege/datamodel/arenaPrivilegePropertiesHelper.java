package com.rw.service.Privilege.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class arenaPrivilegePropertiesHelper extends CfgCsvDao<arenaPrivilegeProperties> {
	public static arenaPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, arenaPrivilegeProperties> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Privilege/arenaPrivilegeProperties.csv",arenaPrivilegeProperties.class);
		Collection<arenaPrivilegeProperties> vals = cfgCacheMap.values();
		for (arenaPrivilegeProperties cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}