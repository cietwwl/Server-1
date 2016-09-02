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
	public boolean isMatchCondition(String userId, int playerLevel, int playerVip, DailyActivityData data) {
		return data.getCurrentProgress() >= needProgress;
	}

}
