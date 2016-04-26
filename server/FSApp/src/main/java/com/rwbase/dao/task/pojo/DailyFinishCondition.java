package com.rwbase.dao.task.pojo;

import com.playerdata.Player;

public interface DailyFinishCondition {

	/**
	 * 检测是否符合完成条件
	 * 
	 * @param player
	 * @return
	 */
	public boolean isMatchCondition(Player player, DailyActivityData data);
}
