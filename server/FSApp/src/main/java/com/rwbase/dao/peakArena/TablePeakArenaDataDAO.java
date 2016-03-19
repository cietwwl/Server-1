package com.rwbase.dao.peakArena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.peakArena.pojo.TablePeakArenaData;



public class TablePeakArenaDataDAO extends DataKVDao<TablePeakArenaData> {

	private static TablePeakArenaDataDAO instance = new TablePeakArenaDataDAO();
	private TablePeakArenaDataDAO(){}
	
	public static TablePeakArenaDataDAO getInstance()
	{
		return instance;
	}
}
