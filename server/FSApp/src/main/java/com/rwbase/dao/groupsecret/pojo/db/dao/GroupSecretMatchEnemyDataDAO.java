package com.rwbase.dao.groupsecret.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;

/*
 * @author HC
 * @date 2016年5月26日 下午5:13:40
 * @Description 秘境匹配到敌人的信息DAO
 */
public class GroupSecretMatchEnemyDataDAO extends DataKVDao<GroupSecretMatchEnemyData> {
	private static GroupSecretMatchEnemyDataDAO dao = new GroupSecretMatchEnemyDataDAO();

	public static GroupSecretMatchEnemyDataDAO getDAO() {
		return dao;
	}

	private GroupSecretMatchEnemyDataDAO() {
	}
}