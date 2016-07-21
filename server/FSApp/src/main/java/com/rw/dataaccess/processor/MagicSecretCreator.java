package com.rw.dataaccess.processor;

import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class MagicSecretCreator implements DataExtensionCreator<UserMagicSecretData> {

	@Override
	public UserMagicSecretData create(String userId) {
		UserMagicSecretData umsData = new UserMagicSecretData(userId);
		return umsData;
	}

}
