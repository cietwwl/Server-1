package com.rwbase.dao.unendingwar;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UnendingWarDAO extends DataKVDao<TableUnendingWar> {
	private static UnendingWarDAO m_instance = new UnendingWarDAO();
	
	public UnendingWarDAO(){
		
	}
	
	public static UnendingWarDAO getInstance(){
		return m_instance;
	}
}
