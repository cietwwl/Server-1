package com.rw.db.tablesBeforeMerge;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.annotation.BPTableName;
import com.rw.log.DBLog;
import com.rw.utils.CommonUtils;

public class BeforeMergeMgr {

	public final static List<AbsTableProcesser> TableList = new ArrayList<AbsTableProcesser>();

	private static BeforeMergeMgr instance = new BeforeMergeMgr();

	public static BeforeMergeMgr getInstance() {
		if (instance == null) {
			instance = new BeforeMergeMgr();
		}
		return instance;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadProcessTable() {
		try {
//			TableList.add(new UserTableProcesser());
//			TableList.add(new GroupDataTableProcesser());
			TableList.add(new RankingTableProcesser());
		} catch (Exception ex) {
			DBLog.LogError("BeforeMegerMgr", ex.getMessage());
		}
	}
	
	public void BeforeMegerProcessTable(DBInfo oriDBInfo, DBInfo tarDBInfo){
		DBLog.LogInfo("BeforeMegerProcessTable", "TableList size:" + TableList.size());
		for (AbsTableProcesser absTableProcesser : TableList) {
			TableInfo oriTableInfo = oriDBInfo.getTableInfo(absTableProcesser.getTableName());
			TableInfo tarTableInfo = tarDBInfo.getTableInfo(absTableProcesser.getTableName());
			absTableProcesser.exec(oriDBInfo, tarDBInfo, oriTableInfo, tarTableInfo);
		}
	}
}
