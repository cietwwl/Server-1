package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:23:47
 * @Description 
 */
public class GroupSecretMatchEnemyDataCreator implements DataExtensionCreator<GroupSecretMatchEnemyData> {

	@Override
	public GroupSecretMatchEnemyData create(String key) {
		GroupSecretMatchEnemyData data = new GroupSecretMatchEnemyData();
		data.setUserId(key);
		return data;
	}
}