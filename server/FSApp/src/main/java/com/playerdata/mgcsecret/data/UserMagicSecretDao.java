package com.playerdata.mgcsecret.data;

import com.rw.fsutil.cacheDao.DataKVDao;



public class UserMagicSecretDao extends DataKVDao<UserMagicSecretData>{
	
	private static UserMagicSecretDao instance  =  new UserMagicSecretDao();
	
	public static UserMagicSecretDao getInstance(){
		return instance;
	}
}
