package com.rw.service.Privilege.datamodel;

import com.rw.fsutil.cacheDao.CfgCsvDao;

public abstract class AbstractPrivilegePropertiesHelper<PrivilegeNameEnum extends Enum<PrivilegeNameEnum>, ConfigClass extends IThresholdConfig>
		extends CfgCsvDao<ConfigClass> implements IPrivilegeThreshold<PrivilegeNameEnum> {
	@Override
	public int getThreshold(PrivilegeNameEnum pname) {
		ConfigClass cfg = cfgCacheMap.get(String.valueOf(pname));
		if (cfg == null)
			return -1;
		int result = cfg.getThreshold();
		if (result <= 0) {
			return -1;
		}
		return result;
	}
}
