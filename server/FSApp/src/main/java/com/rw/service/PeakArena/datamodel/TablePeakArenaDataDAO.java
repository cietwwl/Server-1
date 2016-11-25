package com.rw.service.PeakArena.datamodel;

import com.rw.fsutil.cacheDao.DataKVDao;



public class TablePeakArenaDataDAO extends DataKVDao<TablePeakArenaData> {

	private static TablePeakArenaDataDAO instance = new TablePeakArenaDataDAO();
	protected TablePeakArenaDataDAO(){}
	
	public static TablePeakArenaDataDAO getInstance()
	{
		return instance;
	}
}
