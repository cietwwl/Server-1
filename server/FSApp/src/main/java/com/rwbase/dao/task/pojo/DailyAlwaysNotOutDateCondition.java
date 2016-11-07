package com.rwbase.dao.task.pojo;

import com.rwbase.dao.task.DailyCheckOutDateCondition;

public class DailyAlwaysNotOutDateCondition implements DailyCheckOutDateCondition {

	@Override
	public boolean isOutDate() {
		return false;
	}

}
