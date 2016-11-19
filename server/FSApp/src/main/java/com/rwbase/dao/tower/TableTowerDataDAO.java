package com.rwbase.dao.tower;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.tower.pojo.TableTowerData;

public class TableTowerDataDAO extends DataKVDao<TableTowerData>{

	private static TableTowerDataDAO instance = new TableTowerDataDAO();
	protected TableTowerDataDAO(){}
	
	public static TableTowerDataDAO getInstance()
	{
		return instance;
	}
	
	//public 
}
