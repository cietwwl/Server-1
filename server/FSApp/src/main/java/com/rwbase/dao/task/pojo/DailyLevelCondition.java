package com.rwbase.dao.task.pojo;

import com.rwbase.dao.task.DailyStartCondition;

public class DailyLevelCondition implements DailyStartCondition {

	private final int needLevel;

	public DailyLevelCondition(String text) {
		this.needLevel = Integer.parseInt(text);
	}

	@Override
	public boolean isMatchCondition(String userId, int playerLevel, int playerVip) {
		return playerLevel >= needLevel;
	}

}
