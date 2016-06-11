package com.rwbase.dao.groupsecret.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;

/*
 * @author HC
 * @date 2016年5月27日 下午3:06:05
 * @Description 秘境数据的DAO
 */
public class UserCreateGroupSecretDataDAO extends DataKVDao<UserCreateGroupSecretData> {
	private static UserCreateGroupSecretDataDAO dao = new UserCreateGroupSecretDataDAO();

	public static UserCreateGroupSecretDataDAO getDAO() {
		return dao;
	}

	UserCreateGroupSecretDataDAO() {
	}
}