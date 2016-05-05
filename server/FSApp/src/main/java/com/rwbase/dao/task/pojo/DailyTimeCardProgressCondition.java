package com.rwbase.dao.task.pojo;

import com.playerdata.Player;

/**
 * 检查任务进度的条件(暂时用于完成任务)
 * @author Jamaz
 *
 */
public class DailyTimeCardProgressCondition implements DailyFinishCondition {

	public DailyTimeCardProgressCondition() {
	}

	@Override
	public boolean isMatchCondition(Player player, DailyActivityData data) {
		return true;
	}

}
