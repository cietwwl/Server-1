package com.rwbase.dao.gameworld;

import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.cacheDao.DataKVDao;


public class GameWorldDAO extends DataKVDao<GameWorldAttributeData> {

	private static GameWorldDAO instance;

	public static GameWorldDAO getInstance() {
		if (instance == null) {
			instance = new GameWorldDAO();
		}
		return instance;
	}
	
}
