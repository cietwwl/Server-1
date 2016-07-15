package com.bm.arena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.arena.TableArenaRobotDataDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotData;

/*
 * @author HC
 * @date 2016年7月14日 下午8:38:27
 * @Description 
 */
public class ArenaRobotDataMgr {
	private static ArenaRobotDataMgr mgr = new ArenaRobotDataMgr();

	public static ArenaRobotDataMgr getMgr() {
		return mgr;
	}

	ArenaRobotDataMgr() {
	}

	/**
	 * 获取对应机器人的道术等级列表
	 * 
	 * @param userId
	 * @return
	 */
	public Map<Integer, Integer> getRobotTaoistMap(String userId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return null;
		}

		Map<Integer, Integer> taoistLevelMap = new HashMap<Integer, Integer>();

		int[] taoist = arenaRobotData.getTaoist();
		int len = taoist.length;
		for (int i = 0; i < len; i++) {
			int level = taoist[i];
			if (level <= 0) {
				continue;
			}

			int tag = i + 1;
			List<TaoistMagicCfg> list = TaoistMagicCfgHelper.getInstance().getTaoistCfgListByTag(tag);
			if (list == null || list.isEmpty()) {
				continue;
			}

			for (int j = 0, size = list.size(); j < size; j++) {
				taoistLevelMap.put(list.get(j).getKey(), level);
			}
		}

		return taoistLevelMap;
	}
}