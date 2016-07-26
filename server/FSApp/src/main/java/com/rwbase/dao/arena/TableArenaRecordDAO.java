package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.TableArenaRecord;

public class TableArenaRecordDAO extends DataKVDao<TableArenaRecord> {

	private static TableArenaRecordDAO instance = new TableArenaRecordDAO();

	TableArenaRecordDAO() {
	}

	public static TableArenaRecordDAO getInstance() {
		return instance;
	}
}