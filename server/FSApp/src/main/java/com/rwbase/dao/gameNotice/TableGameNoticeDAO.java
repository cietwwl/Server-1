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
	
	public void save(TableGameNotice notice, boolean insert){
		if(insert){
			super.insertToDB(notice);
		}else{
			super.updateToDB(notice);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void deleteByNoticeId(int noticeId){
		super.deleteToDB(String.valueOf(noticeId));
	}
}
