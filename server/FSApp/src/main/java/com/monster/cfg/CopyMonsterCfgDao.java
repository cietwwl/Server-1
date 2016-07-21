package com.monster.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CopyMonsterCfgDao extends CfgCsvDao<CopyMonsterCfg> {
	
	public static CopyMonsterCfgDao getInstance() {
		return SpringContextUtil.getBean(CopyMonsterCfgDao.class);
	} 
	

	@Override
	protected Map<String, CopyMonsterCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("monster/monster.csv", CopyMonsterCfg.class);
		return cfgCacheMap;
	}
	
	public CopyMonsterCfg getConfig(String key){
		CopyMonsterCfg cfg = getCfgById(key);
		return cfg;
	}

}
