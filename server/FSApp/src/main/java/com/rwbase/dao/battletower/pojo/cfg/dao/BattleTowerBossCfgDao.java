package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.common.Weight;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBossCfg;

/*
 * @author HC
 * @date 2015年9月7日 下午5:34:51
 * @Description 
 */
public class BattleTowerBossCfgDao extends CfgCsvDao<BattleTowerBossCfg> {
	private static BattleTowerBossCfgDao dao;

	public static BattleTowerBossCfgDao getCfgDao() {
		if (dao == null) {
			dao = new BattleTowerBossCfgDao();
		}

		return dao;
	}

	/** Boss按照等级排序,有特殊需求 */
	private TreeMap<Integer, List<String>> bossTreeMap;

	@Override
	public Map<String, BattleTowerBossCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerBossCfg.csv", BattleTowerBossCfg.class);
		bossTreeMap = new TreeMap<Integer, List<String>>();

		for (Entry<String, BattleTowerBossCfg> e : cfgCacheMap.entrySet()) {
			String key = e.getKey();
			BattleTowerBossCfg bossCfg = (BattleTowerBossCfg) e.getValue();
			int level = bossCfg.getLevelLimit();

			List<String> list = bossTreeMap.get(level);
			if (list == null) {
				list = new ArrayList<String>();
				bossTreeMap.put(level, list);
			}

			list.add(key);
		}

		return cfgCacheMap;
	}

	/**
	 * 通过角色等级获取随机出来的Boss信息
	 * 
	 * @param pLevel 角色等级
	 * @return
	 */
	public BattleTowerBossCfg ranBossInfo(int pLevel) {
		if (bossTreeMap == null) {
			initJsonCfg();
		}

		if (bossTreeMap == null) {
			return null;
		}

		Entry<Integer, List<String>> e = bossTreeMap.floorEntry(pLevel);
		if (e == null) {
			return null;
		}

		List<String> list = e.getValue();
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<BattleTowerBossCfg, Integer> proMap = new HashMap<BattleTowerBossCfg, Integer>();
		for (int i = 0, size = list.size(); i < size; i++) {
			String bossId = list.get(i);
			BattleTowerBossCfg bossCfg = (BattleTowerBossCfg) getCfgById(bossId);
			proMap.put(bossCfg, bossCfg.getPro());
		}

		Weight<BattleTowerBossCfg> weight = new Weight<BattleTowerBossCfg>(proMap);
		return weight.getRanResult();
	}
}