package com.playerdata.groupcompetition.util;

import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.rwbase.dao.group.pojo.Group;

public class GCompUpdateGroupInfoTask implements Runnable {

	private Group targetGroup;

	public GCompUpdateGroupInfoTask(Group targetGroup) {
		this.targetGroup = targetGroup;
	}

	@Override
	public void run() {
		GCompEventsDataMgr.getInstance().notifyGroupInfoChange(targetGroup);
		GCompHistoryDataMgr.getInstance().notifyGroupInfoChange(targetGroup);
	}
}
