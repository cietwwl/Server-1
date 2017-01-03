package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Player;
import com.playerdata.teambattle.cfg.MonsterCombinationCfg;
import com.playerdata.teambattle.cfg.MonsterCombinationDAO;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class TeamBattleFullCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		if (UserTeamBattleDataMgr.getInstance().isTeamFull(player)) {
			map.put(RedPointType.TEAMBATTLE_FULL_MEMBER, Collections.EMPTY_LIST);
		}

//		// 检查心魔录可以攻打的红点
//		ServerCommonData scData = ServerCommonDataHolder.getInstance().get();
//		if (null == scData) {
//			return;
//		}
//
//		HashMap<String, String> teamBattleEnimyMap = scData.getTeamBattleEnimyMap();
//		if (teamBattleEnimyMap == null || teamBattleEnimyMap.isEmpty()) {
//			return;
//		}
//
//		List<String> hardIdList = new ArrayList<String>(teamBattleEnimyMap.size());
//		MonsterCombinationDAO instance = MonsterCombinationDAO.getInstance();
//		for (Entry<String, String> e : teamBattleEnimyMap.entrySet()) {
//			MonsterCombinationCfg cfg = instance.getCfgById(e.getValue() + "_1");
//			if (null == cfg) {
//				continue;
//			}
//
//			String hardId = e.getKey();
//			if (UserTeamBattleDataMgr.getInstance().haveFightTimes(player, hardId)) {
//				hardIdList.add(hardId);
//			}
//		}
//
//		if (!hardIdList.isEmpty()) {
//			map.put(RedPointType.TEAMBATLE_CANCHALLENGE, hardIdList);
//		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.TEAM_BATTLE;
	}
}