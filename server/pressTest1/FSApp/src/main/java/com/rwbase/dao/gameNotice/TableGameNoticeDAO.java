package com.rwbase.dao.gameNotice;

import java.util.List;

import com.rw.fsutil.cacheDao.DataRdbDao;

public class TableGameNoticeDAO extends DataRdbDao<TableGameNotice>{
	private static TableGameNoticeDAO instance  = new TableGameNoticeDAO();
	
	private TableGameNoticeDAO(){};
	
	public static TableGameNoticeDAO getInstance(){
		return instance;
	}
	
	public List<TableGameNotice> getAllGameNotice(){
		return this.getAll();
	}
	
	public TableGameNotice getByNoticeId(int id){
		TableGameNotice tableGameNotice = this.findOneByKey("noticeId", id);
		return tableGameNotice;
	}
	
	public boolean saveOrUpdate(TableGameNotice notice){
		return super.saveOrUpdate(notice);
	}
	
	public boolean deleteByNoticeId(int noticeId){
		return super.delete(String.valueOf(noticeId));
	}
}
