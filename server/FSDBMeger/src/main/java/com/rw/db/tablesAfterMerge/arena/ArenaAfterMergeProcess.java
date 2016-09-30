package com.rw.db.tablesAfterMerge.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.constant.ConstantValue;
import com.rw.db.DBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.tablesAfterMerge.AbsAfterMergeProcess;
import com.rw.log.DBLog;

public class ArenaAfterMergeProcess extends AbsAfterMergeProcess {
	
	static List<Integer> rankTypeList = new ArrayList<Integer>();
	
	static{
		rankTypeList.add(1);
		rankTypeList.add(2);
		rankTypeList.add(3);
		rankTypeList.add(4);
		rankTypeList.add(100);
	}

	@Override
	protected void exec(DBInfo dbInfo) {
		long start = System.currentTimeMillis();
		DBLog.LogInfo("ArenaAfterMergeProcess", "ArenaAfterMergeProcess start process");
		Comparator<ArenaExt> comparator = new Comparator<ArenaExt>() {

			@Override
			public int compare(ArenaExt o1, ArenaExt o2) {
				// TODO Auto-generated method stub
				ArenaExtAttribute arenaExtAttribute1 = o1.getExtension();
				ArenaExtAttribute arenaExtAttribute2 = o2.getExtension();
				if (arenaExtAttribute1.getFighting() > arenaExtAttribute2.getFighting()) {
					return -1;
				} else {
					return 1;
				}
			}
		};

		// 竞技场的4个职业和巅峰竞技场
		for (Integer i : rankTypeList) {
			DBLog.LogInfo("ArenaAfterMergeProcess", "ArenaAfterMergeProcess start process rankType:" + i);
			String sql = "select ranking_swap.*, `user`.lastLoginTime as lastLoginTime from ranking_swap left JOIN user on ranking_swap.primary_key = `user`.userId where type = " + i + " order by ranking";

			List<ArenaExt> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[] {}, ArenaExt.class);
			
			if(query == null || query.size() <= 0){
				continue;
			}

			Map<Integer, List<ArenaExt>> map = new LinkedHashMap<Integer, List<ArenaExt>>();

			for (ArenaExt arenaExt : query) {
				int ranking = arenaExt.getRanking();
				if (map.containsKey(ranking)) {
					List<ArenaExt> list = map.get(ranking);
					list.add(arenaExt);
				} else {
					List<ArenaExt> list = new ArrayList<ArenaExt>();
					list.add(arenaExt);
					map.put(ranking, list);
				}
			}

			Map<String, ArenaExt> updateMap = new HashMap<String, ArenaExt>();
			Map<String, ArenaExt> deleteMap = new HashMap<String, ArenaExt>();
			int order = 1;
			long currentTime = System.currentTimeMillis();
			for (Iterator<Entry<Integer, List<ArenaExt>>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, List<ArenaExt>> entry = iterator.next();
				List<ArenaExt> list = entry.getValue();

				Collections.sort(list, comparator);
				for (ArenaExt arenaExt : list) {
					arenaExt.setRanking(order);
					ArenaExtAttribute arenaExtAttribute = arenaExt.getExtension();
					String primary_key = arenaExt.getPrimary_key();
					if (currentTime - arenaExt.getLastLoginTime() > ConstantValue.ARENA_EXPIRE_TIME) {
						deleteMap.put(primary_key, arenaExt);
					} else {
						arenaExtAttribute.setRankLevel(order);
						order++;
						updateMap.put(primary_key, arenaExt);
					}
				}
			}

			// 批量更新数据库
			if (updateMap.size() > 0) {
				DBMgr.getInstance().update(dbInfo.getDBName(), updateMap, ArenaExt.class);
			}
			if (deleteMap.size() > 0) {
				DBMgr.getInstance().delete(dbInfo.getDBName(), deleteMap, ArenaExt.class);
			}
		}
		long end = System.currentTimeMillis();
		DBLog.LogInfo("ArenaAfterMergeProcess", "ArenaAfterMergeProcess end process! cost time:"+(end - start));
	}

}
