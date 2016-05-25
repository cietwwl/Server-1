package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.user.User;

public class FeedbackCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		// TODO Auto-generated method stub
		User user = player.getUserDataMgr().getUser();
		int feedbackId = user.getExtendInfo().getFeedbackId();
		if(feedbackId != 0){
			map.put(RedPointType.GM_FEEDBACK, Collections.EMPTY_LIST);
		}
	}

}
