package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.TableArenaInfo;

public class TableArenaInfoDAO extends DataKVDao<TableArenaInfo> {

	private static TableArenaInfoDAO instance = new TableArenaInfoDAO();

	TableArenaInfoDAO() {
	}

	public static TableArenaInfoDAO getInstance() {
		return instance;
	}
}