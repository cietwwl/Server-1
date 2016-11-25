package com.rw.service.PeakArena.datamodel;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TablePeakArenaRecordDAO extends DataKVDao<TablePeakArenaRecord> {

	private static TablePeakArenaRecordDAO instance = new TablePeakArenaRecordDAO();
	protected TablePeakArenaRecordDAO(){}
	
	public static TablePeakArenaRecordDAO getInstance()
	{
		return instance;
	}
	
}
