package com.rw.db.tablesBeforeMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.annotation.BPTableName;
import com.rw.db.dao.DBMgr;

@BPTableName(name="ranking")
public class RankingTableProcesser extends AbsTableProcesser  {

	@Override
	public void exec(DBInfo oriDBInfo, DBInfo tarDBInfo, TableInfo oriTableInfo, TableInfo tarTableInfo) {
		String sql = "select max(ranking_sequence) as maxId from ranking";
		
		List<Map<String,Object>> query = DBMgr.getInstance().query(oriDBInfo.getDBName(), sql, new Object[]{});
		
		if(query == null || query.size()<=0){
			return;
		}
		
		Map<String, Object> map = query.get(0);
		long maxId = Long.parseLong(map.get("maxId").toString());
		
		String sql2 = "select ranking_sequence from ranking";
		query = DBMgr.getInstance().query(tarDBInfo.getDBName(), sql2, new Object[]{});
		if(query == null || query.size()<=0){
			return;
		}
		
		
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map2 : query) {
			Map<String, Object> temp = new LinkedHashMap<String, Object>();
			for (Iterator<Entry<String, Object>> iterator = map2.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				temp.put(key, ++maxId);
				temp.put("original", entry.getValue());
				updateList.add(temp);
			}
		}
		String sql3 = "update ranking set ranking_sequence = ? where ranking_sequence = ?";
		DBMgr.getInstance().update(tarDBInfo.getDBName(), sql3, updateList);
	}

}
