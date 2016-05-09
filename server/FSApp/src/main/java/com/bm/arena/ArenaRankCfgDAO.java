package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ArenaRankCfgDAO extends CfgCsvDao<ArenaRankCfg> {

	public static ArenaRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaRankCfgDAO.class);
	}

	private HashMap<Integer, ArenaRankEntity> entityMap;
	private List<Integer> allRankIds;
	private TreeMap<Integer, Integer> rankingRewardCountMap;

	@Override
	protected Map<String, ArenaRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaRank.csv", ArenaRankCfg.class);
		HashMap<Integer, ArenaRankEntity> entityMap = new HashMap<Integer, ArenaRankEntity>();
		ArrayList<Integer> rankEntity = new ArrayList<Integer>();
		ArrayList<Integer> rankList = new ArrayList<Integer>();
		for (Map.Entry<String, ArenaRankCfg> entry : cfgCacheMap.entrySet()) {
			ArenaRankCfg cfg = entry.getValue();
			int key = cfg.getKey();
			int rank = cfg.getRank();
			ArenaRankEntity entity = new ArenaRankEntity(key, rank, cfg.getReward());
			entityMap.put(key, entity);
			rankList.add(rank);
			rankEntity.add(key);
		}
		TreeMap<Integer, Integer> rankingRewardCountMap = new TreeMap<Integer, Integer>();
		int count = 0;
		Collections.sort(rankEntity);
		Collections.sort(rankList);
		for (int i = rankList.size(); --i >= 0;) {
			rankingRewardCountMap.put(rankList.get(i), ++count);
		}

		this.rankingRewardCountMap = rankingRewardCountMap;
		for (int i = 0; i < 100; i++) {
			System.out.println("名次：" + i + ",奖励数：" + getRankRewardCount(i));
		}
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

	public int getRankRewardCount(int ranking) {
		if (ranking <= 0) {
			return 0;
		}
		Map.Entry<Integer, Integer> entry = rankingRewardCountMap.ceilingEntry(ranking);
		if (entry == null) {
			return 0;
		}
		return entry.getValue();
	}

}
