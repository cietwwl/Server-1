package com.rw.db.tablesAfterMerge.battleTower;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.db.DBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.dao.kv.DataKvEntity;
import com.rw.db.tablesAfterMerge.AbsAfterMergeProcess;
import com.rw.log.DBLog;
import com.rw.utils.CommonUtils;
import com.rw.utils.jackson.JsonUtil;

public class BattleTowerAfterMergeProcess extends AbsAfterMergeProcess{


	private static String[] sqlArray = new String[10];
	private static String[] updateSqlArray = new String[10];
	
	
	static{
		for (int i = 0; i < sqlArray.length; i++) {
			String sql1 = "select dbvalue, type from table_kvdata0" + i +" where type = 15";
			String sql2 = "update table_kvdata0" + i + " set dbvalue= '%s' where dbkey = '%s' and type = 15";
			sqlArray[i] = sql1;
			updateSqlArray[i] = sql2;
		}
	}
	
	@Override
	protected void exec(DBInfo dbInfo) {
		long start = System.currentTimeMillis();
		DBLog.LogInfo("ArenaAfterMergeProcess", "ArenaAfterMergeProcess start process");
		for (String sql : sqlArray) {
			Map<String, TableBattleTower> updateMap = new HashMap<String, TableBattleTower>();
			List<DataKvEntity> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[]{}, DataKvEntity.class);
			for (DataKvEntity dataKvEntity : query) {
				String dbvalue = dataKvEntity.getDbvalue();
				TableBattleTower battleTower = JsonUtil.readValue(dbvalue, TableBattleTower.class);
				if(battleTower.getSweepState() || battleTower.getSweepStartFloor() != 0 || battleTower.getSweepStartTime() != 0){
					battleTower.setSweepState(false);
					battleTower.setSweepStartFloor(0);
					battleTower.setSweepStartTime(0);
					battleTower.setResetTimes(0);
					updateMap.put(battleTower.getUserId(), battleTower);
				}
			}
			
			for (Iterator<Entry<String, TableBattleTower>> iterator = updateMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, TableBattleTower> entry = iterator.next();
				TableBattleTower value = entry.getValue();
				String userId = entry.getKey();
				String dbValue = JsonUtil.writeValue(value);
				
				int tableIndex = CommonUtils.getTableIndex(userId, updateSqlArray.length);
				String updateSql = updateSqlArray[tableIndex];
				updateSql = String.format(updateSql, dbValue, userId);
				
				DBMgr.getInstance().update(dbInfo.getDBName(), updateSql);
			}
			
			
		}
		long end = System.currentTimeMillis();
		DBLog.LogInfo("EmailAfterMergeProcess", "EmailAfterMergeProcess end process! cost time:"+(end - start));
	}

}
