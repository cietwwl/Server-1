package com.rwbase.dao.zone;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;
import com.rwbase.dao.platformNotice.TablePlatformNotice;

public class TableZoneInfoDAO extends PFDataRdbDao<TableZoneInfo>{

	private static TableZoneInfoDAO instance = new TableZoneInfoDAO();

	public static TableZoneInfoDAO getInstance() {
		return instance;
	}
		
	public List<TableZoneInfo> getAll(){

		return super.getAll();
	}

	public void update(TableZoneInfo tableZoneInfo){
		super.updateToDB(tableZoneInfo);
	}
	
	public TableZoneInfo getByKey(Object key){
		return super.findOneByKey("zoneId", key);
	}
}
