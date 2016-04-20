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
		/**
		 * <pre>
		 * 序章特殊剧情，当我创建完角色之后，登录数据推送完毕，我就直接把剧情设置一个假想值
		 * 保证不管角色当前是故意退出游戏跳过剧情，或者是出现意外退出，在下次进来都不会有剧情的重复问题
		 * </pre>
		 */
		userPlotProgress.getProgressMap().putIfAbsent("0", -1);
		return userPlotProgress;
	}

}
