package com.rwbase.dao.Army;

import com.rw.fsutil.cacheDao.DataKVCacheDao;


public class UserArmyDataDAO extends DataKVCacheDao<UserArmyData> {
	private static UserArmyDataDAO m_instance = new UserArmyDataDAO();
	private UserArmyDataDAO(){}
	
	public static UserArmyDataDAO getInstance(){
		return m_instance;
	}

	
}