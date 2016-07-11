package com.playerdata.groupFightOnline.data;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UserGFightOnlineDAO extends DataKVDao<UserGFightOnlineData>{
	private static UserGFightOnlineDAO instance = new UserGFightOnlineDAO();

	public static UserGFightOnlineDAO getInstance() {
		return instance;
	}

	private UserGFightOnlineDAO() { }
}
