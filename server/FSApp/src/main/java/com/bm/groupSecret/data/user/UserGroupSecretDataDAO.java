package com.bm.groupSecret.data.user;

import com.rw.fsutil.cacheDao.DataKVDao;

public final class UserGroupSecretDataDAO extends DataKVDao<UserGroupSecretData> {

	private static UserGroupSecretDataDAO instance = new UserGroupSecretDataDAO();

	public static UserGroupSecretDataDAO getInstance() {
		return instance;
	}
}