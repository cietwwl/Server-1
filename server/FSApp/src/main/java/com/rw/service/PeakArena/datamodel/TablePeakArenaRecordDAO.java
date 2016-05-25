package com.rw.service.PeakArena.datamodel;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TablePeakArenaRecordDAO extends DataKVDao<TablePeakArenaRecord> {

	private static TablePeakArenaRecordDAO instance;
	private TablePeakArenaRecordDAO(){}
	
	public static TablePeakArenaRecordDAO getInstance()
	{
		if(instance == null){
			instance = new TablePeakArenaRecordDAO();
		}
		return instance;
	}
	
}
