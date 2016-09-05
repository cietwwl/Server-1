package com.rwbase.dao.task.pojo;


public interface DailyFinishCondition {

	/**
	 * 检测是否符合完成条件
	 * 
	 * @param player
	 * @return
	 */
	public boolean isMatchCondition(String userId, int playerLevel, int playerVip, DailyActivityData data);
}
