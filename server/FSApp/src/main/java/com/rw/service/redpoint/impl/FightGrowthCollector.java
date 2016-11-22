package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.FSUserFightingGrowthData;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FightGrowthCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		FSUserFightingGrowthData userFightingGrowthData = FSUserFightingGrowthDataDAO.getInstance().get(player.getUserId());
		FSUserFightingGrowthTitleCfg nextTitleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		if (nextTitleCfg != null && player.getHeroMgr().getFightingTeam(player) >= nextTitleCfg.getFightingRequired()) {
			List<String> list = new ArrayList<String>(1);
			list.add(nextTitleCfg.getKey());
			map.put(RedPointType.HOME_TOP_FIGHTING_GROWTH, list);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

}
