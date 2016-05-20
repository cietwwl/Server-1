package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.guide.pojo.UserPlotProgress;

public class PlotProgressCreator implements DataExtensionCreator<UserPlotProgress> {

	@Override
	public UserPlotProgress create(String userId) {
		UserPlotProgress userPlotProgress = new UserPlotProgress();
		userPlotProgress.setUserId(userId);
//		/**
//		 * <pre>
//		 * 序章特殊剧情，当我创建完角色之后，登录数据推送完毕，我就直接把剧情设置一个假想值
//		 * 保证不管角色当前是故意退出游戏跳过剧情，或者是出现意外退出，在下次进来都不会有剧情的重复问题
//		 * </pre>
//		 */
//		userPlotProgress.getProgressMap().putIfAbsent("0", -1);
		return userPlotProgress;
	}

}
