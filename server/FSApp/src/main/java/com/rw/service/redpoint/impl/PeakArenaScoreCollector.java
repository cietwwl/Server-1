package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.peakarena.PeakArenaScoreRewardCfgDAO;

public class PeakArenaScoreCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		TablePeakArenaData arenaData = TablePeakArenaDataDAO.getInstance().get(player.getUserId());
		if (arenaData == null) {
			return;
		}
		checkScore(arenaData, level, map);
	}

	//检查是否有分数奖励
	private void checkScore(TablePeakArenaData arenaData, int level, Map<RedPointType, List<String>> map) {
		int score = arenaData.getScore();
		if (score <= 0) {
			return;
		}
		int count = PeakArenaScoreRewardCfgDAO.getInstance().getRewardCount(score);
		if (count == 0) {
			return;
		}
		if (count > arenaData.getRewardList().size()) {
			List<String> list = Collections.emptyList();
			map.put(RedPointType.PEAK_ARENA_SCORE_REWARD, list);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.PEAK_ARENA;
	}

}
