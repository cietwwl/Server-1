package com.rw.db.tablesAfterMerge.groupSecret;

import com.rw.db.DBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.tablesAfterMerge.AbsAfterMergeProcess;
import com.rw.dblog.DBLog;

/**
 * 
 * @author lida
 *
 */
public class GroupSecretAfterMergeProcess extends AbsAfterMergeProcess{

	@Override
	protected void exec(DBInfo dbInfo) {
		long start = System.currentTimeMillis();
		DBLog.LogInfo("GroupSecretAfterMergeProcess", "GroupSecretAfterMergeProcess start process");
		for(int i= 0; i<=9; i++){
			String sql = "DELETE FROM table_kvdata0"+i+" WHERE TYPE >= 21 AND TYPE <= 25;";
			DBMgr.getInstance().update(dbInfo.getDBName(), sql);
		}
		
		String sql = "DELETE FROM ranking WHERE TYPE = 24;";
		DBMgr.getInstance().update(dbInfo.getDBName(), sql);
		
		long end = System.currentTimeMillis();
		DBLog.LogInfo("GroupSecretAfterMergeProcess", "GroupSecretAfterMergeProcess end process! cost time:"+(end - start));
	}

}
