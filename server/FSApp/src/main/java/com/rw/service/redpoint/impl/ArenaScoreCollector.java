package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.arena.ArenaRankCfgDAO;
import com.bm.arena.ArenaScoreCfgDAO;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ArenaScoreCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		if (player.getTempAttribute().checkAndResetArenaChanged()) {
			return;
		}
		TableArenaData arenaData = TableArenaDataDAO.getInstance().get(player.getUserId());
		if (arenaData == null) {
			return;
		}
		checkScore(arenaData, level, map);
		checkHisReward(arenaData, level, map);
	}

	//检查是否有分数奖励
	private void checkScore(TableArenaData arenaData, int level, Map<RedPointType, List<String>> map) {
		int score = arenaData.getScore();
		if (score <= 0) {
			return;
		}
		int count = ArenaScoreCfgDAO.getInstance().getRewardCount(score);
		if (count == 0) {
			return;
		}
		if (count > arenaData.getRewardList().size()) {
			map.put(RedPointType.ARENA_WINDOW_SUM_POINT, Collections.EMPTY_LIST);
		}
	}

	//检查是否有历史奖励
	private void checkHisReward(TableArenaData arenaData, int level, Map<RedPointType, List<String>> map) {
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
