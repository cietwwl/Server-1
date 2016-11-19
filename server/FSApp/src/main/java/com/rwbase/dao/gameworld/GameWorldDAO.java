package com.rwbase.dao.gameworld;

import com.rw.fsutil.cacheDao.DataKVDao;


public class GameWorldDAO extends DataKVDao<GameWorldAttributeData> {

	private static GameWorldDAO instance = new GameWorldDAO();

	public static GameWorldDAO getInstance() {
		return instance;
	}
	
}
