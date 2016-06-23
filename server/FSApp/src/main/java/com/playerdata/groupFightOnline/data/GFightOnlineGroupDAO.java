package com.playerdata.groupFightOnline.data;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GFightOnlineGroupDAO extends DataKVDao<GFightOnlineGroupData>{
	private static GFightOnlineGroupDAO instance = new GFightOnlineGroupDAO();

	public static GFightOnlineGroupDAO getInstance() {
		return instance;
	}

	private GFightOnlineGroupDAO() {
	}
}
