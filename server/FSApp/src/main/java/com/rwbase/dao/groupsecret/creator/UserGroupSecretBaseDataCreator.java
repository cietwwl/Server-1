package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:23:00
 * @Description 
 */
public class UserGroupSecretBaseDataCreator implements DataExtensionCreator<UserGroupSecretBaseData> {

	@Override
	public UserGroupSecretBaseData create(String key) {
		UserGroupSecretBaseData data = new UserGroupSecretBaseData();
		data.setUserId(key);
		return data;
	}
}