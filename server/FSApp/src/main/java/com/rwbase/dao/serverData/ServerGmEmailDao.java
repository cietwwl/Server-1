package com.rwbase.dao.serverData;

import java.util.List;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.email.EmailData;

public class ServerGmEmailDao extends DataRdbDao<ServerGmEmail>{
	private static ServerGmEmailDao m_instance = new ServerGmEmailDao();
	
	public static ServerGmEmailDao getInstance(){
		return m_instance;
	}
	
	public List<ServerGmEmail> getAllMails(){
		return this.getAll();
	}
	
	public void save(ServerGmEmail email, boolean insert){
		if(insert){
			insertToDB(email);
		}else{
			updateToDB(email);
		}
	}
}
