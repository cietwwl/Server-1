package com.rwbase.dao.guildSecretArea;

import com.rw.fsutil.cacheDao.DataKVDao;


public class SecretAreaUserInfoDAO extends DataKVDao<SecretAreaUserInfo> {
	private static SecretAreaUserInfoDAO m_instance = new SecretAreaUserInfoDAO();
	private SecretAreaUserInfoDAO(){}
	
	public static SecretAreaUserInfoDAO getInstance(){
		return m_instance;
	}
}