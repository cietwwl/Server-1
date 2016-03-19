package com.rwbase.dao.gulid.faction;

import com.rw.fsutil.cacheDao.DataKVCacheDao;


public class GuildDAO extends DataKVCacheDao<Guild> {
	
	private static GuildDAO m_instance = new GuildDAO();
	
	private GuildDAO(){}
	
	public static GuildDAO getInstance(){
		return m_instance;
	}

	
	
}