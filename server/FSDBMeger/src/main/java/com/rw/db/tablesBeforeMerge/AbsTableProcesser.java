package com.rw.db.tablesBeforeMerge;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.annotation.BPTableName;

public abstract class AbsTableProcesser {
	
	protected String processName;
	
	public abstract void exec(DBInfo oriDBInfo, DBInfo tarDBInfo, TableInfo oriTableInfo, TableInfo tarTableInfo);
	
	

	
	public String getTableName() {
		// TODO Auto-generated method stub
		BPTableName annotation = this.getClass().getAnnotation(BPTableName.class);
		return annotation.name();
	}
}
