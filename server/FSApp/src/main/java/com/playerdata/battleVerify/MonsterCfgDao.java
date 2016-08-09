package com.playerdata.battleVerify;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MonsterCfgDao extends CfgCsvDao<MonsterCfg> {
	
	public static MonsterCfgDao getInstance() {
		return SpringContextUtil.getBean(MonsterCfgDao.class);
	} 
	

	@Override
	protected Map<String, MonsterCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battle/monster.csv", MonsterCfg.class);
		return cfgCacheMap;
	}
	
	public MonsterCfg getConfig(String key){
		MonsterCfg cfg = getCfgById(key);
		return cfg;
	}

}
