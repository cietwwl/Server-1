package com.playerdata.groupcompetition.holder.data;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompTeamMember {

	private boolean isLeader;
	private ArmyInfoSimple teamInfo;
	
	public GCompTeamMember(boolean pIsLeader, ArmyInfoSimple pTeamInfo) {
		this.isLeader = pIsLeader;
		this.teamInfo = pTeamInfo;
	}
	
	public boolean isLeader() {
		return this.isLeader;
	}
	
	public String getUserId() {
		return teamInfo.getPlayer().getId();
	}
	
	public ArmyInfoSimple getTeamInfo() {
		return teamInfo;
	}
}
