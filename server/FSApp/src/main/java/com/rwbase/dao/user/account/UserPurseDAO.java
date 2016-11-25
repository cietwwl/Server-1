package com.rwbase.dao.user.account;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UserPurseDAO extends DataKVDao<UserPurse> {
	private static UserPurseDAO instance = new UserPurseDAO();
	protected UserPurseDAO(){}
	public static UserPurseDAO getInstance(){
		return instance;
	}

}
