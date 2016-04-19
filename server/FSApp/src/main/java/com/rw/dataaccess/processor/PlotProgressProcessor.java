package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.guide.pojo.UserPlotProgress;

public class PlotProgressProcessor implements PlayerCreatedProcessor<UserPlotProgress> {

	@Override
	public UserPlotProgress create(PlayerCreatedParam param) {
		UserPlotProgress userPlotProgress = new UserPlotProgress();
		userPlotProgress = new UserPlotProgress();
		userPlotProgress.setUserId(param.getUserId());
		return userPlotProgress;
	}

}
