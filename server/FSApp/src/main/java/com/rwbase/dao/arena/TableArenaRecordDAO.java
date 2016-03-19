package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.TableArenaRecord;

public class TableArenaRecordDAO extends DataKVDao<TableArenaRecord> {

	private static TableArenaRecordDAO instance;
	
	private TableArenaRecordDAO() {}
	
	public static TableArenaRecordDAO getInstance()
	{
		if(instance == null){
			instance = new TableArenaRecordDAO();
		}
		return instance;
	}
	
}
