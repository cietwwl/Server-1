package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaScoreCfgDAO;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ArenaScoreCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.ARENA, player.getLevel())) {
			return;
		}
		TableArenaData arenaData = TableArenaDataDAO.getInstance().get(player.getUserId());
		if (arenaData == null) {
			return;
		}
		int score = arenaData.getScore();
		if (score <= 0) {
			return;
		}
		int count = ArenaScoreCfgDAO.getInstance().getRewardCount(score);
		if (count == 0) {
			return;
		}
		if(count > arenaData.getRewardList().size()){
			map.put(RedPointType.ARENA_WINDOW_SUM_POINT, Collections.EMPTY_LIST);
		}
	}

}
