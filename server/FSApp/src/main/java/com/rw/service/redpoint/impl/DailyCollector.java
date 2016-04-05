package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.task.pojo.DailyActivityData;

public class DailyCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		int level = player.getLevel();
		CfgOpenLevelLimit dailyOpenLevel = (CfgOpenLevelLimit) CfgOpenLevelLimitDAO
				.getInstance().getCfgById(
						String.valueOf(eOpenLevelType.DAILY.getOrder()));
		if (dailyOpenLevel == null || level >= dailyOpenLevel.getMinLevel()) {
			// //日常可领取
			boolean dailyCompleted = false;
			List<DailyActivityData> dailyList = player.getDailyActivityMgr()
					.getAllTask();
			for (DailyActivityData data : dailyList) {
				if (data.getCanGetReward() == 1) {
					dailyCompleted = true;
				}
			}
			if (dailyCompleted) {
				map.put(RedPointType.HOME_WINDOW_DAILY, Collections.EMPTY_LIST);
			}
		}
	}

}
