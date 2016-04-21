package com.rwbase.dao.task;

import com.playerdata.Player;

public interface DailyStartCondition {


	/**
	 * 检测是否符合开启条件
	 * 
	 * @param player
	 * @return
	 */
	public boolean isMatchCondition(Player player);
}
