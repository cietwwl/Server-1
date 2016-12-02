package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.playerdata.WorshipMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class WorshipCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		if (!DateUtils.dayChanged(GameManager.getOpenTime())) {
			// 开服第一天不显示膜拜的红点
			return;
		}

		if (!WorshipMgr.getInstance().isWorship(player)) {
			return;
		}

		map.put(RedPointType.RANKING_ARENA_CHAMPION, RankingMgr.getInstance().getWorshipList());
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.WORSHIP;
	}

}
