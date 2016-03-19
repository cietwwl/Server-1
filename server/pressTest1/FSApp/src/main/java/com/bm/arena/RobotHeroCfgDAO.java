package com.bm.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rwbase.common.config.CfgCsvHelper;

public class RobotHeroCfgDAO extends CfgCsvDao<RobotHeroCfg> {

	private static RobotHeroCfgDAO instance = new RobotHeroCfgDAO();

	public static RobotHeroCfgDAO getInstance() {
		return instance;
	}

	public HashMap<String, List<RobotHeroCfg>> map;

	@Override
	public Map<String, RobotHeroCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arenaRobot/RobotHeroCfg.csv", RobotHeroCfg.class);
		HashMap<String, List<RobotHeroCfg>> map_ = new HashMap<String, List<RobotHeroCfg>>();
		for (Entry<String, RobotHeroCfg> entry : cfgCacheMap.entrySet()) {
			// map_.put(key, value)
			String first = entry.getKey().split("_")[0];
			List<RobotHeroCfg> list = map_.get(first);
			if (list == null) {
				list = new ArrayList<RobotHeroCfg>();
				map_.put(first, list);
			}
			list.add((RobotHeroCfg) entry.getValue());
		}
		map = map_;
		return cfgCacheMap;
	}

	public List<RobotHeroCfg> getRobotHeroCfg(String prefix) {
		if (map == null) {
			initJsonCfg();
		}
		return map.get(prefix);
	}
}
