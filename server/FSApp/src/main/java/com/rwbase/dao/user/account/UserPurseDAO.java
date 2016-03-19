package com.rwbase.dao.user.account;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UserPurseDAO extends DataKVDao<UserPurse> {
	private static UserPurseDAO instance = new UserPurseDAO();
	private UserPurseDAO(){}
	public static UserPurseDAO getInstance(){
		return instance;
	}

}
