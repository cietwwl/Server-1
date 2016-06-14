package com.rwbase.dao.arena;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bm.arena.RobotCfgDAO;
import com.common.HPCUtil;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaCost;
import com.rwbase.dao.arena.pojo.ArenaCostCfg;

public class ArenaCostCfgDAO extends CfgCsvDao<ArenaCostCfg> {

	private static ArenaCostCfgDAO instance;
	private TreeMap<Integer, ArenaCost> map;

	public static ArenaCostCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaCostCfgDAO.class);
	}	

	@Override
	public Map<String, ArenaCostCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaCost.csv", ArenaCostCfg.class);
		TreeMap<Integer, ArenaCost> map_ = new TreeMap<Integer, ArenaCost>();
		for (ArenaCostCfg cfg : cfgCacheMap.values()) {
			List<Integer> list = HPCUtil.parseIntegerList(cfg.getTimes(), "~");
			int size = list.size();
			int times = list.get(0);
			if (size == 1) {
				ArenaCost cost = new ArenaCost();
				cost.setResetCost(cfg.getCost());
				cost.setBuyTimesCost(cfg.getBuyTimesCost());
				cost.setTimes(times);
				map_.put(times, cost);
			} else {
				int end = list.get(1);
				for (int i = times; i <= end; i++) {
					ArenaCost cost = new ArenaCost();
					cost.setResetCost(cfg.getCost());
					cost.setBuyTimesCost(cfg.getBuyTimesCost());
					cost.setTimes(i);
					map_.put(times, cost);
				}
			}
		}
		map = map_;
		return cfgCacheMap;
	}

	public ArenaCost get(int times) {
		if (map == null) {
			initJsonCfg();
		}
		ArenaCost cfg = map.get(times);
		if (cfg != null) {
			return cfg;
		}
		Integer leastKey = map.floorKey(times);
		if (leastKey != null) {
			return map.get(leastKey);
		}
		return null;
	}
}
