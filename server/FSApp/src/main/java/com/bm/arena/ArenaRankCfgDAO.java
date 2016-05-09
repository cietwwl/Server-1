package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ArenaRankCfgDAO extends CfgCsvDao<ArenaRankCfg> {

	public static ArenaRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaRankCfgDAO.class);
	}

	private HashMap<Integer, ArenaRankEntity> entityMap;
	private List<Integer> allRankIds;

	@Override
	protected Map<String, ArenaRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaRank.csv", ArenaRankCfg.class);
		HashMap<Integer, ArenaRankEntity> entityMap = new HashMap<Integer, ArenaRankEntity>();
		ArrayList<Integer> rankEntity = new ArrayList<Integer>();
		for (Map.Entry<String, ArenaRankCfg> entry : cfgCacheMap.entrySet()) {
			ArenaRankCfg cfg = entry.getValue();
			int key = cfg.getKey();
			ArenaRankEntity entity = new ArenaRankEntity(key, cfg.getRank(), cfg.getReward());
			entityMap.put(key, entity);
			rankEntity.add(key);
		}
		Collections.sort(rankEntity);
		this.allRankIds = Collections.unmodifiableList(rankEntity);
		this.entityMap = entityMap;
		return cfgCacheMap;
	}

	public ArenaRankEntity getArenaRankEntity(int key) {
		return this.entityMap.get(key);
	}

	public List<Integer> getAllRankIds() {
		return allRankIds;
	}
	
}
