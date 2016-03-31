package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ArenaCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.ARENA, player.getLevel())) {
			return;
		}
		if (player.getTempAttribute().isRecordChanged()) {
			map.put(RedPointType.ARENA_WINDOW_BATTLE_LOG, Collections.EMPTY_LIST);
		}
	}

}
