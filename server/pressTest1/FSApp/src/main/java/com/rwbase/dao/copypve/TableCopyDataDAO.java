package com.rwbase.dao.copypve;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.copypve.pojo.TableCopyData;

public class TableCopyDataDAO extends DataKVDao<TableCopyData> {

	private static TableCopyDataDAO instance = new TableCopyDataDAO();
	private TableCopyDataDAO(){}
	
	public static TableCopyDataDAO getInstance()
	{
		return instance;
	}
	
}
