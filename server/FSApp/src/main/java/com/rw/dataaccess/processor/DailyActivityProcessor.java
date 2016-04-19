package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

public class DailyActivityProcessor implements PlayerCreatedProcessor<DailyActivityTaskItem> {

	@Override
	public DailyActivityTaskItem create(PlayerCreatedParam param) {
		DailyActivityTaskItem taskItem = new DailyActivityTaskItem();
		taskItem.setUserId(param.getUserId());
		return taskItem;
	}

}
