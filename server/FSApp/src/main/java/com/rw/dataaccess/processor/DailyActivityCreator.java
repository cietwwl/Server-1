package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

public class DailyActivityCreator implements DataExtensionCreator<DailyActivityTaskItem> {

	@Override
	public DailyActivityTaskItem create(String userId) {
		DailyActivityTaskItem taskItem = new DailyActivityTaskItem();
		taskItem.setUserId(userId);
		return taskItem;
	}

}
