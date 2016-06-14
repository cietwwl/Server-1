package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:22:30
 * @Description 
 */
public class UserCreateGroupSecretDataCreator implements DataExtensionCreator<UserCreateGroupSecretData> {

	@Override
	public UserCreateGroupSecretData create(String key) {
		UserCreateGroupSecretData data = new UserCreateGroupSecretData();
		data.setUserId(key);
		return data;
	}
}