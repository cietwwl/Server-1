package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MonsterCombinationDAO extends CfgCsvDao<MonsterCombinationCfg> {
	public static MonsterCombinationDAO getInstance() {
		return SpringContextUtil.getBean(MonsterCombinationDAO.class);
	}

	@Override
	public Map<String, MonsterCombinationCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("teamBattle/monsterCombination.csv",MonsterCombinationCfg.class);
		Collection<MonsterCombinationCfg> vals = cfgCacheMap.values();
		for (MonsterCombinationCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
