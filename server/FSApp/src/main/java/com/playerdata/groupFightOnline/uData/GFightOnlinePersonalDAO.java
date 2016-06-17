package com.playerdata.groupFightOnline.uData;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GFightOnlinePersonalDAO extends DataKVDao<GFightOnlinePersonalDAO>{
	private static GFightOnlinePersonalDAO instance = new GFightOnlinePersonalDAO();

	public static GFightOnlinePersonalDAO getInstance() {
		return instance;
	}

	private GFightOnlinePersonalDAO() { }

	
}
