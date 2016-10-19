package com.rw.handler.groupCompetition.data.team;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataSyn.SynItem;


public class GCompTeam implements SynItem {

	/**
	 * 
	 * 队伍类型
	 * 
	 * @author CHEN.P
	 *
	 */
	public static enum GCompTeamType {
		/**
		 * 多人玩家队伍
		 */
		MULTIPLE_PLAYERS(1),
		/**
		 * 单人玩家队伍
		 */
		SINGLE_PLAYER(2), ;
		public final int sign;

		private GCompTeamType(int pSign) {
			this.sign = pSign;
		}
	}

	private String teamId; // 队伍的id

	private List<GCompTeamMember> members; // 队伍的成员]
	private String leaderId; // 队长的id，需要同步到客户端
	private boolean randomTeam;

	public static GCompTeam createNewTeam(String teamId, GCompTeamType pType, GCompTeamMember leader, GCompTeamMember... members) {
		GCompTeam team = new GCompTeam();
		team.teamId = teamId;
		team.members = new ArrayList<GCompTeamMember>();
		team.members.add(leader);
		if (members != null && members.length > 0) {
			for (int i = 0; i < members.length; i++) {
				team.members.add(members[i]);
			}
		}
		team.leaderId = leader.getUserId();
		return team;
	}
	
	@Override
	public String getId() {
		return teamId;
	}

	public GCompTeamMember getTeamMember(String userId) {
		synchronized (members) {
			for (GCompTeamMember teamMember : members) {
				if (teamMember.getUserId().equals(userId)) {
					return teamMember;
				}
			}
			return null;
		}
	}

	public void addTeamMember(GCompTeamMember member) {
		synchronized (members) {
			members.add(member);
		}
	}

	public void removeTeamMember(GCompTeamMember member) {
		synchronized (members) {
			members.remove(member);
		}
	}

	public void setLeaderId(String pLeaderId) {
		this.leaderId = pLeaderId;
	}

	public String getLeaderId() {
		return leaderId;
	}
	
	public boolean isRandomTeam() {
		return randomTeam;
	}

	public void setRandomTeam(boolean randomTeam) {
		this.randomTeam = randomTeam;
	}
	
	public int getMemberSize() {
		return this.members.size();
	}
}

