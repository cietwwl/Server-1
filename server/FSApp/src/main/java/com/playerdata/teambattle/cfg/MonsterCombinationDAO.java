package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MonsterCombinationDAO extends CfgCsvDao<MonsterCombinationCfg> {
	private Map<String, Map<Integer, MonsterCombinationCfg>> loopMap = new HashMap<String, Map<Integer, MonsterCombinationCfg>>();
	
	public static MonsterCombinationDAO getInstance() {
		return SpringContextUtil.getBean(MonsterCombinationDAO.class);
	}

	@Override
	public Map<String, MonsterCombinationCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("teamBattle/monsterCombination.csv",MonsterCombinationCfg.class);
		Collection<MonsterCombinationCfg> vals = cfgCacheMap.values();
		for (MonsterCombinationCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			Map<Integer, MonsterCombinationCfg> loopValues = loopMap.get(cfg.getId());
			if(loopValues == null) {
				loopValues = new HashMap<Integer, MonsterCombinationCfg>();
				loopMap.put(cfg.getId(), loopValues);
			}
			loopValues.put(cfg.getBattleTime(), cfg);
		}
		return cfgCacheMap;
	}
	
	public Map<Integer, MonsterCombinationCfg> getLoopValues(String loopID){
		return loopMap.get(loopID);
	}
}
