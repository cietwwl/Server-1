package com.rwbase.dao.task.pojo;

import com.playerdata.Player;
import com.rwbase.dao.task.DailyStartCondition;

public class DailyLevelCondition implements DailyStartCondition {

	private final int needLevel;

	public DailyLevelCondition(String text) {
		this.needLevel = Integer.parseInt(text);
	}

	@Override
	public boolean isMatchCondition(Player player) {
		return player.getLevel() >= needLevel;
	}

}
