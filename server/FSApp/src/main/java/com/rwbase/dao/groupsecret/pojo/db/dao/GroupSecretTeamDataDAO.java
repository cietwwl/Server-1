package com.rwbase.dao.groupsecret.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;

/*
 * @author HC
 * @date 2016年5月26日 下午5:04:20
 * @Description 使用阵容信息的DAO
 */
public class GroupSecretTeamDataDAO extends DataKVDao<GroupSecretTeamData> {
	private static GroupSecretTeamDataDAO dao = new GroupSecretTeamDataDAO();

	public static GroupSecretTeamDataDAO getDAO() {
		return dao;
	}

	private GroupSecretTeamDataDAO() {
	}
}