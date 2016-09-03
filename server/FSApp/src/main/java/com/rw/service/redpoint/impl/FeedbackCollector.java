package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.user.User;

public class FeedbackCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		User user = player.getUserDataMgr().getUser();
		int feedbackId = user.getExtendInfo().getFeedbackId();
		if(feedbackId != 0){
			map.put(RedPointType.GM_FEEDBACK, Collections.EMPTY_LIST);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

}
