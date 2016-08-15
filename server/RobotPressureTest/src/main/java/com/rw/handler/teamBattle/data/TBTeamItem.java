package com.rw.handler.teamBattle.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.dataSyn.SynItem;
import com.rw.handler.teamBattle.dataForClient.StaticMemberTeamInfo;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class TBTeamItem implements SynItem{
	
	private String teamID;  // armyID = hardID_UUID
	
	private String hardID;
	
	private List<TeamMember> members = new ArrayList<TeamMember>();
	
	private String leaderID;
	
	private boolean canFreeJion = true;
	
	private List<StaticMemberTeamInfo> teamMembers = new ArrayList<StaticMemberTeamInfo>();

	@Override
	public String getId() {
		return teamID;
	}
	
	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}

	public String getHardID() {
		return hardID;
	}

	public void setHardID(String hardID) {
		this.hardID = hardID;
	}

	public String getLeaderID() {
		return leaderID;
	}

	public void setLeaderID(String leaderID) {
		this.leaderID = leaderID;
	}
	
	public List<TeamMember> getMembers(){
		return Collections.unmodifiableList(members);
	}

	public boolean isCanFreeJion() {
		return canFreeJion;
	}

	public void setCanFreeJion(boolean canFreeJion) {
		this.canFreeJion = canFreeJion;
	}

	public List<StaticMemberTeamInfo> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<StaticMemberTeamInfo> teamMembers) {
		this.teamMembers = teamMembers;
	}
}
