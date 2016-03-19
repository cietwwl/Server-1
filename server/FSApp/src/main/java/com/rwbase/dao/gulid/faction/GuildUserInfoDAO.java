package com.rwbase.dao.gulid.faction;

import com.rw.fsutil.cacheDao.DataKVDao;


public class GuildUserInfoDAO extends DataKVDao<GuildUserInfo> {
	private static GuildUserInfoDAO m_instance = new GuildUserInfoDAO();
	private GuildUserInfoDAO(){}
	
	public static GuildUserInfoDAO getInstance(){
		return m_instance;
	}
}