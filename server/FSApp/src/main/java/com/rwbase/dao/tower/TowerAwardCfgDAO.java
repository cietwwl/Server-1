package com.rwbase.dao.tower;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class TowerAwardCfgDAO extends CfgCsvDao<TowerAwardCfg> {

	public static TowerAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(TowerAwardCfgDAO.class);
	}

	private TreeMap<Integer, Map<Integer, String>> awardMap;

	private TowerAwardCfgDAO() {
	}

	@Override
	public Map<String, TowerAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerAward.csv", TowerAwardCfg.class);

		TreeMap<Integer, Map<Integer, String>> awardMap = new TreeMap<Integer, Map<Integer, String>>();
		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			for (Entry<String, TowerAwardCfg> e : cfgCacheMap.entrySet()) {
				TowerAwardCfg awardCfg = (TowerAwardCfg) e.getValue();
				Map<Integer, String> map = awardMap.get(awardCfg.level);
				if (map == null) {
					map = new HashMap<Integer, String>();
					awardMap.put(awardCfg.level, map);
				}

				map.put(awardCfg.towerId, e.getKey());
			}
		}

		this.awardMap = awardMap;
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
		Entry<Integer, Map<Integer, String>> e = getInstance().awardMap.floorEntry(level);
		if (e == null) {
			return null;
		}

		Map<Integer, String> value = e.getValue();
		if (value == null) {
			return null;
		}

		String id = value.get(floor + 1);
		if (id == null) {
			return null;
		}

		return (TowerAwardCfg) getInstance().getCfgById(id);
	}
}