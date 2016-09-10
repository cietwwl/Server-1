package com.rwbase.dao.task;

public interface DailyStartCondition {

	/**
	 * 检测是否符合开启条件
	 * 
	 * @param player
	 * @return
	 */
	public boolean isMatchCondition(String userId, int playerLevel, int playerVip);
}
