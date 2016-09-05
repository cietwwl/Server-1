package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FresherActivityCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		// 检查开服活动
		List<String> fresherActivity = player.getFresherActivityMgr().getFresherActivityList();
		if (fresherActivity != null && !fresherActivity.isEmpty()) {
			map.put(RedPointType.HOME_WINDOW_OPEN_GIGT, fresherActivity);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

}
