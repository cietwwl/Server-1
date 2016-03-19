package com.rwbase.dao.guildSecretArea;

import com.rw.fsutil.cacheDao.DataKVDao;


public class SecretAreaBattleInfoDAO extends DataKVDao<SecretAreaBattleInfo> {
	private static SecretAreaBattleInfoDAO m_instance = new SecretAreaBattleInfoDAO();
	private SecretAreaBattleInfoDAO(){}
	
	public static SecretAreaBattleInfoDAO getInstance(){
		return m_instance;
	}
}