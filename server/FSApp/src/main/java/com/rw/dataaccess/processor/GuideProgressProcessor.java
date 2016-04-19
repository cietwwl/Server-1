package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.guide.pojo.UserGuideProgress;

public class GuideProgressProcessor implements PlayerCreatedProcessor<UserGuideProgress> {

	@Override
	public UserGuideProgress create(PlayerCreatedParam param) {
		UserGuideProgress userGuideProgress = new UserGuideProgress();
		userGuideProgress.setUserId(param.getUserId());
		return userGuideProgress;

	}

}
