package com.rwbase.dao.task;

public interface DailyCheckOutDateCondition {

	/**
	 * 
	 * 今天是否已经过时了
	 * 
	 * @return
	 */
	public boolean isOutDate();
}
