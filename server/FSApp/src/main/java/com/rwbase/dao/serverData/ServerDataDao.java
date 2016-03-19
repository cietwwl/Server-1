package com.rwbase.dao.serverData;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.email.EmailData;




public class ServerDataDao extends  DataKVDao<ServerData>{

	private static ServerDataDao m_instance = new ServerDataDao();
	
	public static ServerDataDao getInstance(){
		return m_instance;
	}
}
