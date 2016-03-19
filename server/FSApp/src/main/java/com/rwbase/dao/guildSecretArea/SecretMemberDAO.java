package com.rwbase.dao.guildSecretArea;

import com.rw.fsutil.cacheDao.DataKVDao;

public class SecretMemberDAO extends DataKVDao<TableSecretMember> {
	private static SecretMemberDAO m_instance = new SecretMemberDAO();
	public SecretMemberDAO(){
		
	}
	
	public static SecretMemberDAO getInstance(){
		return m_instance;
	}
}


