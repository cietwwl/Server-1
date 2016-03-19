package com.rwbase.dao.platformNotice;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TablePlatformNoticeDAO extends PFDataRdbDao<TablePlatformNotice>{
	private static TablePlatformNoticeDAO instance = new TablePlatformNoticeDAO();
	
	private TablePlatformNoticeDAO(){};
	
	public static TablePlatformNoticeDAO getInstance(){
		return instance;
	}
	
	public TablePlatformNotice getPlatformNotice(){
		List<TablePlatformNotice> all = this.getAll();
		if(all.size()>0){
			return all.get(0);
		}
		return null;
	}
	
	public TablePlatformNotice getById(int id){
		TablePlatformNotice platformNotice = this.findOneByKey("id", id);
		return platformNotice;
	}
	
	public boolean saveOrUpdate(TablePlatformNotice notice){
		return super.saveOrUpdate(notice);
	}
}
