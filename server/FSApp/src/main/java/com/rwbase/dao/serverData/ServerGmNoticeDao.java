package com.rwbase.dao.serverData;

import java.util.List;

import com.rw.fsutil.cacheDao.DataRdbDao;

public class ServerGmNoticeDao extends DataRdbDao<ServerGmNotice>{
	private static ServerGmNoticeDao m_instance = new ServerGmNoticeDao();
	
	public static ServerGmNoticeDao getInstance(){
		return m_instance;
	}
	
	public List<ServerGmNotice> getAllNotices(){
		return this.getAll();
	}
	
	public void save(ServerGmNotice gmNotice, boolean insert){
		if(insert){
			insertToDB(gmNotice);
		}else{
			updateToDB(gmNotice);
		}
	}
	
	public void remove(String key){
		deleteToDB(key);
	}
}
