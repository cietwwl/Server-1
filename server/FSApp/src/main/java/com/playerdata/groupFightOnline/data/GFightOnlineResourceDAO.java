package com.playerdata.groupFightOnline.data;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GFightOnlineResourceDAO extends DataKVDao<GFightOnlineResourceData>{
	private static GFightOnlineResourceDAO instance = new GFightOnlineResourceDAO();

	public static GFightOnlineResourceDAO getInstance() {
		return instance;
	}

	private GFightOnlineResourceDAO() {
	}
	
}
