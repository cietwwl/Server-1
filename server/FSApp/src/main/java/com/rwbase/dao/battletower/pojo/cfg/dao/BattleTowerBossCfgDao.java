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
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBossTemplate;

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
	private TreeMap<Integer, List<Integer>> bossTreeMap;
	private Map<Integer, BattleTowerBossTemplate> bossTmpMap;

	@Override
	public Map<String, BattleTowerBossCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerBossCfg.csv", BattleTowerBossCfg.class);

		TreeMap<Integer, List<Integer>> bossTreeMap = new TreeMap<Integer, List<Integer>>();
		Map<Integer, BattleTowerBossTemplate> bossTmpMap = new HashMap<Integer, BattleTowerBossTemplate>();

		for (Entry<String, BattleTowerBossCfg> e : cfgCacheMap.entrySet()) {
			BattleTowerBossCfg bossCfg = (BattleTowerBossCfg) e.getValue();
			int level = bossCfg.getLevelLimit();

			List<Integer> list = bossTreeMap.get(level);
			if (list == null) {
				list = new ArrayList<Integer>();
				bossTreeMap.put(level, list);
			}

			list.add(bossCfg.getBossId());

			bossTmpMap.put(bossCfg.getBossId(), new BattleTowerBossTemplate(bossCfg));
		}

		this.bossTreeMap = bossTreeMap;
		this.bossTmpMap = bossTmpMap;

		return cfgCacheMap;
	}

	/**
	 * 通过角色等级获取随机出来的Boss信息
	 * 
	 * @param pLevel
	 *            角色等级
	 * @return
	 */
	public BattleTowerBossTemplate ranBossInfo(int pLevel) {
		if (bossTreeMap == null) {
			initJsonCfg();
		}

		if (bossTreeMap == null) {
			return null;
		}

		if (bossTmpMap == null) {
			return null;
		}

		Entry<Integer, List<Integer>> e = bossTreeMap.floorEntry(pLevel);
		if (e == null) {
			return null;
		}

		List<Integer> list = e.getValue();
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<BattleTowerBossTemplate, Integer> proMap = new HashMap<BattleTowerBossTemplate, Integer>();
		for (int i = 0, size = list.size(); i < size; i++) {
			Integer bossId = list.get(i);
			BattleTowerBossTemplate battleTowerBossTemplate = bossTmpMap.get(bossId);
			if (battleTowerBossTemplate == null) {
				continue;
			}

			proMap.put(battleTowerBossTemplate, battleTowerBossTemplate.getPro());
		}

		Weight<BattleTowerBossTemplate> weight = new Weight<BattleTowerBossTemplate>(proMap);
		return weight.getRanResult();
	}

	/**
	 * 获取试练塔Boss的模版
	 * 
	 * @param bossId
	 * @return
	 */
	public BattleTowerBossTemplate getBossTemplate(int bossId) {
		if (bossTmpMap == null) {
			return null;
		}

		return bossTmpMap.get(bossId);
	}
}