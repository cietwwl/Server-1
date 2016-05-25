package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.guide.pojo.UserGuideProgress;

public class GuideProgressCreator implements DataExtensionCreator<UserGuideProgress> {

	@Override
	public UserGuideProgress create(String userId) {
		UserGuideProgress userGuideProgress = new UserGuideProgress();
		userGuideProgress.setUserId(userId);
		return userGuideProgress;

	}

}
