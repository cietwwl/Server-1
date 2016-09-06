package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.FriendMgr;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;

public class FriendCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		FriendMgr friendMgr = player.getFriendMgr();
		if (friendMgr.hasReceivePower()) {
			map.put(RedPointType.FRIEND_WINDOW_RECEIVE_POWER, Collections.EMPTY_LIST);
		}
		if (friendMgr.hasRequest()) {
			map.put(RedPointType.FRIEND_WINDOW_ADD_FRIEND, Collections.EMPTY_LIST);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.FRIEND;
	}

}
