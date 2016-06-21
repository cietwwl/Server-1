package com.playerdata.groupFightOnline.uData;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UserGFightOnlineDAO extends DataKVDao<UserGFightOnlineDAO>{
	private static UserGFightOnlineDAO instance = new UserGFightOnlineDAO();

	public static UserGFightOnlineDAO getInstance() {
		return instance;
	}

	private UserGFightOnlineDAO() { }

	
}
