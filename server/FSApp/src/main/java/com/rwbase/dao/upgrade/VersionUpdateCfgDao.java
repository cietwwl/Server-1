package com.rwbase.dao.upgrade;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class VersionUpdateCfgDao extends CfgCsvDao<VersionUpdateCfg>{
	
	public static VersionUpdateCfgDao getInstance() {
		return SpringContextUtil.getBean(VersionUpdateCfgDao.class);
	}
	
	private VersionUpdateCfgDao(){
		initJsonCfg();
	}

	@Override
	public Map<String, VersionUpdateCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("versionupdate/versionupdate.csv",VersionUpdateCfg.class);
		return cfgCacheMap;
	}

	public VersionUpdateCfg getCfgByKey(String key) {
		VersionUpdateCfg cfg = (VersionUpdateCfg) getCfgById(key);
		return cfg;
	}
	
}
