package com.rwbase.dao.groupsecret.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;

/*
 * @author HC
 * @date 2016年5月26日 下午4:58:55
 * @Description 秘境个人数据的DAO
 */
public class UserGroupSecretBaseDataDAO extends DataKVDao<UserGroupSecretBaseData> {
	private static UserGroupSecretBaseDataDAO dao = new UserGroupSecretBaseDataDAO();

	public static UserGroupSecretBaseDataDAO getDAO() {
		return dao;
	}

	private UserGroupSecretBaseDataDAO() {
	}
}