package com.rwbase.dao.email;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableEmailDAO  extends DataKVDao<TableEmail> {
	private static TableEmailDAO m_instance = new TableEmailDAO();
	
	public static TableEmailDAO getInstance(){
		return m_instance;
	}
}
