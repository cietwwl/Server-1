package com.rwbase.dao.dropitem;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.common.DataAccessTimeoutException;

public class DropRecordDAO extends DataKVDao<DropRecord> {

	private static DropRecordDAO instance = new DropRecordDAO();
	
	public static DropRecordDAO getInstance() {
		return instance;
	}

	public DropRecord getDropRecord(final String userId) throws DataAccessTimeoutException {
		return super.get(userId);
	}
}
