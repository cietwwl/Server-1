package com.playerdata.groupFightOnline.uData;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GFightOnlineResourceDAO extends DataKVDao<GFightOnlineResourceData>{
	private static GFightOnlineResourceDAO instance = new GFightOnlineResourceDAO();

	public static GFightOnlineResourceDAO getInstance() {
		return instance;
	}

	private GFightOnlineResourceDAO() {
	}
	
}
