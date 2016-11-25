package com.rw.service.gamble.datamodel;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GambleHotHeroPlanDAO extends DataKVDao<GambleHotHeroPlan> {
	private static GambleHotHeroPlanDAO instance = new GambleHotHeroPlanDAO();

	public static GambleHotHeroPlanDAO getInstance() {
		return instance;
	}

}
