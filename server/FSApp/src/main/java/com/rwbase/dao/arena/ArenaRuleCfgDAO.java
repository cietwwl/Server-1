package com.rwbase.dao.arena;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaRuleCfg;

public class ArenaRuleCfgDAO extends CfgCsvDao<ArenaRuleCfg> {

	@Override
	public Map<String, ArenaRuleCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaRule.csv", ArenaRuleCfg.class);
		return cfgCacheMap;
	}

}
