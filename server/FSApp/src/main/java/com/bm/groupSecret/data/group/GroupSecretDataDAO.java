package com.bm.groupSecret.data.group;

import com.rw.fsutil.cacheDao.DataKVDao;


public final class GroupSecretDataDAO extends DataKVDao<GroupSecretData> {
	
	private static GroupSecretDataDAO instance = new GroupSecretDataDAO();

	public static GroupSecretDataDAO getInstance() {
		return instance;
	}


}