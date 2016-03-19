package com.rwbase.dao.zone;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TableZoneInfoDAO extends PFDataRdbDao<TableZoneInfo>{

	private static TableZoneInfoDAO instance = new TableZoneInfoDAO();

	public static TableZoneInfoDAO getInstance() {
		return instance;
	}
		
	public List<TableZoneInfo> getAll(){

		return super.getAll();
	}


}
