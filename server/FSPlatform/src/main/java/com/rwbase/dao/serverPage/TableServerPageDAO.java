package com.rwbase.dao.serverPage;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TableServerPageDAO extends PFDataRdbDao<TableServerPage>{
	
	public static final int COMMAND_PAGE = -2;
	public static final int USER_PAGE = -1;
	public static final int TEST_PAGE = 0;
	
	private static TableServerPageDAO instance = new TableServerPageDAO();
	
	public static TableServerPageDAO getInstance(){
		return instance;
	}
	
	public List<TableServerPage> getAll(){
		return super.getAll();
	}
}
