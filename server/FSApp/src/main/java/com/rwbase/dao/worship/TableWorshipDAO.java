package com.rwbase.dao.worship;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableWorshipDAO extends DataKVDao<TableWorship> {
	private static TableWorshipDAO m_instance = new TableWorshipDAO();
	
	public TableWorshipDAO(){
		
	}
	
	public static TableWorshipDAO getInstance(){
		return m_instance;
	}
}