package com.rwbase.dao.guildSecretArea;

import com.rw.fsutil.cacheDao.DataKVDao;


public class SecretAreaDAO extends DataKVDao<TableSecretArea> {
	private static SecretAreaDAO m_instance = new SecretAreaDAO();
	private SecretAreaDAO(){}
	
	public static SecretAreaDAO getInstance(){
		return m_instance;
	}
}