package com.rwbase.dao.platform;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.peakArena.PeakArenaScoreCfgDAO;
import com.rwbase.dao.platform.pojo.PlatformConfig;

public class PlatformConfigDAO extends CfgCsvDao<PlatformConfig> {
	public static PlatformConfigDAO getInstance() {
		return SpringContextUtil.getBean(PlatformConfigDAO.class);
	}

	@Override
	public Map<String, PlatformConfig> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Platform/PlatformConfig.csv", PlatformConfig.class);
		return cfgCacheMap;
	}
}
