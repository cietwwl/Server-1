package com.rwbase.dao.user;

import com.rw.fsutil.cacheDao.DataKVDao;



public class UserGameDataDao extends DataKVDao<UserGameData>{
	
	private static UserGameDataDao instance  =  new UserGameDataDao();
	
	private UserGameDataDao(){super();};
	
	public static UserGameDataDao getInstance(){
		return instance;
	}
}
