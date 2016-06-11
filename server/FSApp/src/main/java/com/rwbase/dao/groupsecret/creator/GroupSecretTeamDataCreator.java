package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:24:29
 * @Description 
 */
public class GroupSecretTeamDataCreator implements DataExtensionCreator<GroupSecretTeamData> {

	@Override
	public GroupSecretTeamData create(String key) {
		GroupSecretTeamData data = new GroupSecretTeamData();
		data.setUserId(key);
		return data;
	}
}