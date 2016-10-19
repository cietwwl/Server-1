package com.playerdata.groupcompetition.util;

import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;

public class GCompGroupMemberLeaveTask implements Runnable {

	private String targetGroupId;
	private String targetUserId;
	
	public GCompGroupMemberLeaveTask(String targetGroupId, String targetUserId) {
		this.targetGroupId = targetGroupId;
		this.targetUserId = targetUserId;
	}

	@Override
	public void run() {
		GCompMemberMgr.getInstance().removeGCompMember(targetGroupId, targetUserId);
		GCompTeamMgr.getInstance().onGroupMemberLeave(targetGroupId);
	}

}
