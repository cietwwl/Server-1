package com.rwbase.dao.user;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.user.pojo.TableUniqueName;

public class UserUniqueNameDAO extends DataKVDao<TableUniqueName>{
	
	private static UserUniqueNameDAO instance  =  new UserUniqueNameDAO();
	
	private UserUniqueNameDAO(){};
	
	public static UserUniqueNameDAO getInstance(){
		return instance;
	}

}
