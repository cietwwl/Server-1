package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.TableArenaData;

public class TableArenaDataDAO extends DataKVDao<TableArenaData> {

	private static TableArenaDataDAO instance = new TableArenaDataDAO();

	TableArenaDataDAO() {
	}

	public static TableArenaDataDAO getInstance() {
		return instance;
	}
}