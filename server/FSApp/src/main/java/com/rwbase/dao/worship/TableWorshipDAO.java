package com.rwbase.dao.worship;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableWorshipDAO extends DataKVDao<TableWorship> {
	private static TableWorshipDAO m_instance = new TableWorshipDAO();
	
	public TableWorshipDAO(){
		
	}
	
	public static TableWorshipDAO getInstance(){
		return m_instance;
	}

	@Override
	public TableWorship get(String id) {
		TableWorship ship = super.get(id);
		if(ship == null){
			ship = new TableWorship();
			ship.setCareer(Integer.parseInt(id));
		}
		return ship;
		
	}
	
	
}