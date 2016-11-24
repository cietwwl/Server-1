package com.bm.worldBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBSettingCfgDAO extends CfgCsvDao<WBSettingCfg> {	


	public static WBSettingCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBSettingCfgDAO.class);
	}

	@Override
	public Map<String, WBSettingCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBSettingCfg.csv", WBSettingCfg.class);
		return cfgCacheMap;
	}


	final static String id = "1";
	public WBSettingCfg getCfg(){
		return getCfgById(id);
	}

}