package com.rw.service.gamble.datamodel;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GambleHotHeroPlanDAO extends DataKVDao<GambleHotHeroPlan> {
	private static GambleHotHeroPlanDAO instance;

	public static GambleHotHeroPlanDAO getInstance() {
		if (instance == null) {
			instance = new GambleHotHeroPlanDAO();
		}
		return instance;
	}

}
