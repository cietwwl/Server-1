package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.team.TeamInfo;

@SynClass
public class GCompTeamMemberSynData {

	private boolean isLeader;
	private TeamInfo teamInfo;
	
	public boolean isLeader() {
		return this.isLeader;
	}
	
	public String getUserId() {
		return teamInfo.getUuid();
	}
	
	public TeamInfo getTeamInfo() {
		return teamInfo;
	}
}
