package com.bm.arena;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ArenaRankCfgDAO extends CfgCsvDao<ArenaRankCfg> {

	public static ArenaRankCfgDAO getInstance(){
		return SpringContextUtil.getBean(ArenaRankCfgDAO.class);
	}
	
	private HashMap<Integer, ArenaRankEntity> entityMap;

	@Override
	protected Map<String, ArenaRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaRank.csv", ArenaRankCfg.class);
		HashMap<Integer, ArenaRankEntity> entityMap = new HashMap<Integer, ArenaRankEntity>();
		for (Map.Entry<String, ArenaRankCfg> entry : cfgCacheMap.entrySet()) {
			ArenaRankCfg cfg = entry.getValue();
			int key = cfg.getKey();
			ArenaRankEntity entity = new ArenaRankEntity(key, cfg.getRank(), cfg.getReward());
			entityMap.put(key, entity);
		}
		this.entityMap = entityMap;
		return cfgCacheMap;
	}

	public ArenaRankEntity getArenaRankEntity(int key){
		return this.entityMap.get(key);
	}
	
}
