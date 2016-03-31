package com.bm.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ArenaScoreCfgDAO extends CfgCsvDao<ArenaScore> {

	private HashMap<Integer, ArenaScoreTemplate> templateMap;

	@Override
	protected Map<String, ArenaScore> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaScore.csv", ArenaScore.class);
		templateMap = new HashMap<Integer, ArenaScoreTemplate>(cfgCacheMap.size());
		for (Map.Entry<String, ArenaScore> entry : cfgCacheMap.entrySet()) {
			String key = entry.getKey();
			ArenaScore arenaScore = entry.getValue();
			ArenaScoreTemplate template = new ArenaScoreTemplate(arenaScore.getScore(), arenaScore.getReward());
			this.templateMap.put(Integer.parseInt(key), template);
		}
		return cfgCacheMap;
	}
	
	public static ArenaScoreCfgDAO getInstance(){
		return SpringContextUtil.getBean(ArenaScoreCfgDAO.class);
	}

	public ArenaScoreTemplate getScoreTemplate(int key){
		return templateMap.get(key);
	}
}
