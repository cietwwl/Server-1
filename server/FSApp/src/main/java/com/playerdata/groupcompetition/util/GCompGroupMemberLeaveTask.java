package com.playerdata.groupcompetition.util;

import com.playerdata.groupcompetition.holder.GCompMemberMgr;

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
	}

}
