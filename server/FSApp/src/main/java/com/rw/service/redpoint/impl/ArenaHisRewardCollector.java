package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.arena.ArenaRankCfgDAO;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ArenaHisRewardCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map,int level) {
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.ARENA, player)) {
			return;
		}
		TableArenaData arenaData = TableArenaDataDAO.getInstance().get(player.getUserId());
		if (arenaData == null) {
			return;
		}
		int maxRanking = ArenaBM.getInstance().getMaxPlace(arenaData);
		if (maxRanking <= 0) {
			return;
		}
		int count = ArenaRankCfgDAO.getInstance().getRankRewardCount(maxRanking);
		if (count == 0) {
			return;
		}
		if (count > arenaData.getHistoryRewards().size()) {
			map.put(RedPointType.ARENA_WINDOW_RANK_REWARD_POINT, Collections.EMPTY_LIST);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.ARENA;
	}

}
