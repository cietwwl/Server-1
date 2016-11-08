package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.playerdata.WorshipMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataDao;

public class WorshipCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		UserGameData userGameData = UserGameDataDao.getInstance().get(player.getUserId(), true);
		if (!WorshipMgr.getInstance().isWorship(userGameData.getLastWorshipTime())) {
			return;
		}
		map.put(RedPointType.RANKING_ARENA_CHAMPION, RankingMgr.getInstance().getWorshipList());
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.WORSHIP;
	}

}
