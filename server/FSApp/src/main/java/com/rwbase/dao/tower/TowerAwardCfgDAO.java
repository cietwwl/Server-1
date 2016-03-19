package com.rwbase.dao.tower;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public final class TowerAwardCfgDAO extends CfgCsvDao<TowerAwardCfg> {
	private static TowerAwardCfgDAO dao = new TowerAwardCfgDAO();

	private TreeMap<Integer, Map<Integer, String>> awardMap;

	private TowerAwardCfgDAO() {
		awardMap = new TreeMap<Integer, Map<Integer, String>>();
		Map<String, TowerAwardCfg> maps = getMaps();
		if (maps != null && !maps.isEmpty()) {
			for (Entry<String, TowerAwardCfg> e : maps.entrySet()) {
				TowerAwardCfg awardCfg = (TowerAwardCfg) e.getValue();
				Map<Integer, String> map = awardMap.get(awardCfg.level);
				if (map == null) {
					map = new HashMap<Integer, String>();
					awardMap.put(awardCfg.level, map);
				}

				map.put(awardCfg.towerId, e.getKey());
			}
		}
	}

	@Override
	public Map<String, TowerAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerAward.csv",TowerAwardCfg.class);
		return cfgCacheMap;
	}

	// private List<TowerAwardCfg> getTowerCfgListByLevel(int level) {// 35/5 = 7
	// int cigLevel = (level / 5) * 5;
	// List<TowerAwardCfg> cfgList = new ArrayList<TowerAwardCfg>();
	// for (TowerAwardCfg cfg : getAllCfg()) {
	// if (cfg.level == cigLevel) {
	// cfgList.add(cfg);
	// }
	// }
	// return cfgList;
	// }

	/**
	 * 获取奖励信息
	 * 
	 * @param level
	 * @param floor
	 * @return
	 */
	public static TowerAwardCfg getLevelTowerCfgByFloor(int level, int floor) {
		Entry<Integer, Map<Integer, String>> e = dao.awardMap.floorEntry(level);
		if (e == null) {
			return null;
		}

		Map<Integer, String> value = e.getValue();
		if (value == null) {
			return null;
		}

		String id = value.get(floor);
		if (id == null) {
			return null;
		}

		return (TowerAwardCfg) dao.getCfgById(id);
	}
}