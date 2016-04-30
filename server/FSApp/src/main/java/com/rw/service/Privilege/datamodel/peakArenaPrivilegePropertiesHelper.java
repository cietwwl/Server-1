package com.rw.service.Privilege.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class peakArenaPrivilegePropertiesHelper extends CfgCsvDao<peakArenaPrivilegeProperties> {
	public static peakArenaPrivilegePropertiesHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrivilegePropertiesHelper.class);
	}

	@Override
	public Map<String, peakArenaPrivilegeProperties> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Privilege/peakArenaPrivilegeProperties.csv",peakArenaPrivilegeProperties.class);
		Collection<peakArenaPrivilegeProperties> vals = cfgCacheMap.values();
		for (peakArenaPrivilegeProperties cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}