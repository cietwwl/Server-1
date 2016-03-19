package com.rwbase.dao.peakArena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.peakArena.pojo.TablePeakArenaRecord;

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
