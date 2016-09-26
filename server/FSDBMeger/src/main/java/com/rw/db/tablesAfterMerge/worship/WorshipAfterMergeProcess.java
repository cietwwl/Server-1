package com.rw.db.tablesAfterMerge.worship;

import java.util.ArrayList;
import java.util.List;

import com.rw.db.DBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.tablesAfterMerge.AbsAfterMergeProcess;
import com.rw.dblog.DBLog;

public class WorshipAfterMergeProcess extends AbsAfterMergeProcess{

	static List<Integer> TypeList = new ArrayList<Integer>();
	
	static{
		TypeList.add(10);
		TypeList.add(12);
		TypeList.add(14);
		TypeList.add(16);
	}
	
	@Override
	protected void exec(DBInfo dbInfo) {
		
		long start = System.currentTimeMillis();
		DBLog.LogInfo("WorshipAfterMergeProcess", "WorshipAfterMergeProcess start process");
		String sql = "delete from ranking where type = ";
		
		for (Integer type : TypeList) {
			String tempsql= sql + type;
			DBMgr.getInstance().update(dbInfo.getDBName(), tempsql);
		}
		
		long end = System.currentTimeMillis();
		DBLog.LogInfo("WorshipAfterMergeProcess", "WorshipAfterMergeProcess end process! cost time:"+(end - start));
	}

}
