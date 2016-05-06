package com.fy.version.activity;

import java.util.Map;

import com.fy.utils.CfgCsvDao;
import com.fy.utils.CfgCsvHelper;

public class VersionUpdateCfgDao extends CfgCsvDao<VersionUpdateCfg>{
	private static VersionUpdateCfgDao instance = new VersionUpdateCfgDao();
	
	public static VersionUpdateCfgDao getInstance() {
		if(instance == null){
			instance = new VersionUpdateCfgDao();
		}
		return instance;
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
