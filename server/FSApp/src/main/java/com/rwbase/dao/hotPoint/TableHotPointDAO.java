package com.rwbase.dao.hotPoint;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableHotPointDAO extends DataKVDao<TableHotPoint> {
	private static TableHotPointDAO m_instance = new TableHotPointDAO();
	
	public TableHotPointDAO(){
		
	}
	
	public static TableHotPointDAO getInstance(){
		return m_instance;
	}
}
