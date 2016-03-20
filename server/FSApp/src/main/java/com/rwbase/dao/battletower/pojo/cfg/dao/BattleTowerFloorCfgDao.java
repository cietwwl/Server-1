package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerFloorCfg;

/*
 * @author HC
 * @date 2015年9月3日 上午11:01:00
 * @Description 试练塔层模版
 */
public class BattleTowerFloorCfgDao extends CfgCsvDao<BattleTowerFloorCfg> {
	private Map<String, List<Integer>> groupMap;

	public static BattleTowerFloorCfgDao getCfgDao() {
		return SpringContextUtil.getBean(BattleTowerFloorCfgDao.class);
	}

	@Override
	public Map<String, BattleTowerFloorCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerFloorCfg.csv", BattleTowerFloorCfg.class);
		// 初始化所有的里程碑Id
		groupMap = new HashMap<String, List<Integer>>();
		for (Entry<String, BattleTowerFloorCfg> e : cfgCacheMap.entrySet()) {
			BattleTowerFloorCfg cfg = (BattleTowerFloorCfg) e.getValue();
			String groupId = String.valueOf(cfg.getGroupId());

			List<Integer> list = groupMap.get(groupId);
			if (list == null) {
				list = new ArrayList<Integer>();
				groupMap.put(groupId, list);
			}

			list.add(cfg.getFloor());
		}

		// 找到所有的里程碑中包含的层数信息
		return cfgCacheMap;
	}

	/**
	 * 获取试练塔里程碑列表Id
	 * 
	 * @return
	 */
	public List<String> getGroupList() {
		if (groupMap == null) {
			initJsonCfg();
		}

		return groupMap == null ? new ArrayList<String>() : new ArrayList<String>(groupMap.keySet());
	}

	/**
	 * 获取某个里程碑中包含的数据
	 * 
	 * @param groupId
	 * @return
	 */
	public List<Integer> getContainFloorList(String groupId) {
		if (groupMap == null) {
			initJsonCfg();
		}

		if (groupMap == null) {
			return new ArrayList<Integer>();
		}

		List<Integer> list = groupMap.get(groupId);
		if (list == null) {
			return new ArrayList<Integer>();
		}

		Collections.sort(list);
		return new ArrayList<Integer>(list);
	}
}