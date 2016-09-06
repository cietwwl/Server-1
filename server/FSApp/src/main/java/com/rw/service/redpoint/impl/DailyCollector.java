package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.task.pojo.DailyActivityData;

public class DailyCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		// //日常可领取
		boolean dailyCompleted = false;
		List<DailyActivityData> dailyList = player.getDailyActivityMgr().getAllTask();
		for (DailyActivityData data : dailyList) {
			if (data.getCanGetReward() == 1) {
				dailyCompleted = true;
				break;
			}
		}
		if (dailyCompleted) {
			map.put(RedPointType.HOME_WINDOW_DAILY, Collections.EMPTY_LIST);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.DAILY;
	}

}
