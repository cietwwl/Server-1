package com.rwbase.dao.tower;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.tower.pojo.TableTowerData;

public class TableTowerDataDAO extends DataKVDao<TableTowerData>{

	private static TableTowerDataDAO instance;
	private TableTowerDataDAO(){}
	
	public static TableTowerDataDAO getInstance()
	{
		if(instance == null){
			instance = new TableTowerDataDAO();
		}
		return instance;
	}
	
	//public 
}
