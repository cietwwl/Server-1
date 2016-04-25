package com.rwbase.dao.task.pojo;

import com.playerdata.Player;

/**
 * 检查任务进度的条件(暂时用于完成任务)
 * @author Jamaz
 *
 */
public class DailyProgressCondition implements DailyFinishCondition {

	private final int needProgress;

	public DailyProgressCondition(String text) {
		this.needProgress = Integer.parseInt(text);
	}

	@Override
	public boolean isMatchCondition(Player player, DailyActivityData data) {
		return data.getCurrentProgress() >= needProgress;
	}

}
