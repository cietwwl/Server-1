package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rw.service.redpoint.RedPointType;

public class TeamBattleFullCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if (UserTeamBattleDataMgr.getInstance().isTeamFull(player)) {
			map.put(RedPointType.TEAM_BATTLE_TEAM_FULL, Collections.EMPTY_LIST);
		}
	}
}
