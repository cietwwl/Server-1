package com.rwbase.dao.groupsecret.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretDefendRecordData;

/*
 * @author HC
 * @date 2016年5月26日 下午5:08:49
 * @Description 防守记录DAO
 */
public class GroupSecretDefendRecordDataDAO extends DataKVDao<GroupSecretDefendRecordData> {

	private static GroupSecretDefendRecordDataDAO dao = new GroupSecretDefendRecordDataDAO();

	public static GroupSecretDefendRecordDataDAO getDAO() {
		return dao;
	}

	private GroupSecretDefendRecordDataDAO() {
	}
}