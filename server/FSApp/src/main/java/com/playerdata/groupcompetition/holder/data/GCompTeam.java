package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 
 * 帮派战的队伍数据
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class GCompTeam {

	private String teamId; // 队伍的id
	private List<GCompTeamMember> members; // 队伍的成员
	@IgnoreSynField
	private List<GCompTeamMember> membersRO;
	
	public static GCompTeam createNewTeam(String teamId, GCompTeamMember leader, GCompTeamMember... members) {
		GCompTeam team = new GCompTeam();
		team.teamId = teamId;
		team.members = new ArrayList<GCompTeamMember>();
		team.members.add(leader);
		team.membersRO = Collections.unmodifiableList(team.members);
		if (members != null && members.length > 0) {
			for (int i = 0; i < members.length; i++) {
				team.members.add(members[i]);
			}
		}
		return team;
	}
	
	public String getTeamId() {
		return this.teamId;
	}
	
	public List<GCompTeamMember> getMembers() {
		return membersRO;
	}
	
	public GCompTeamMember getTeamMember(String userId) {
		synchronized(members) {
			for(GCompTeamMember teamMember : members) {
				if(teamMember.getUserId().equals(userId)) {
					return teamMember;
				}
			}
			return null;
		}
	}
	
	public void addTeamMember(GCompTeamMember member) {
		synchronized(members) {
			members.add(member);
		}
	}
}
