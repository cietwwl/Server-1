package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.arena.pojo.TableArenaInfo;

public class TableArenaInfoDAO extends DataKVDao<TableArenaInfo>{

	private static TableArenaInfoDAO instance;
	private TableArenaInfoDAO(){}
	
	public static TableArenaInfoDAO getInstance()
	{
		if(instance == null){
			instance = new TableArenaInfoDAO();
		}
		return instance;
	}
	
	//public 
}
