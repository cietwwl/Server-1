package com.rwbase.dao.peakArena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.peakArena.pojo.TablePeakArenaData;



public class TablePeakArenaDataDAO extends DataKVDao<TablePeakArenaData> {

	private static TablePeakArenaDataDAO instance;
	private TablePeakArenaDataDAO(){}
	
	public static TablePeakArenaDataDAO getInstance()
	{
		if(instance == null){
			instance = new TablePeakArenaDataDAO();
		}
		return instance;
	}
}
