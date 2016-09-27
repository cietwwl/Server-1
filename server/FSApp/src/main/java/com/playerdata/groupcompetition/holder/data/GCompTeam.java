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

	private List<GCompTeamMember> members; // 队伍的成员
	@IgnoreSynField
	private List<GCompTeamMember> membersRO;
	@IgnoreSynField
	private boolean matching; // 是否正在匹配中
	@IgnoreSynField
	private boolean inBattle; // 是否正在战斗中
	private String leaderId; // 队长的id，需要同步到客户端
	private int lv; // 等级，取队长等级
	private int battleTimes;
	private GCompTeamType teamType;
	private String descr;

	public static GCompTeam createNewTeam(String teamId, GCompTeamType pType, GCompTeamMember leader, GCompTeamMember... members) {
		GCompTeam team = new GCompTeam();
		team.teamId = teamId;
		team.members = new ArrayList<GCompTeamMember>();
		team.members.add(leader);
		team.membersRO = Collections.unmodifiableList(team.members);
		team.lv = leader.getArmyInfo().getPlayer().getLevel();
		if (members != null && members.length > 0) {
			for (int i = 0; i < members.length; i++) {
				team.members.add(members[i]);
			}
		}
		team.leaderId = leader.getUserId();
		team.teamType = pType;
		team.descr = "GCompTeam [teamId=" + teamId + ", teamType=" + pType + " , members=" + team.members + "]";
		return team;
	}

	public String getTeamId() {
		return this.teamId;
	}

	public List<GCompTeamMember> getMembers() {
		return membersRO;
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

	public boolean isMatching() {
		return matching;
	}

	public void setMatching(boolean value) {
		synchronized (members) {
			this.matching = value;
		}
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(boolean value) {
		this.inBattle = value;
	}

	public void setLeaderId(String pLeaderId) {
		this.leaderId = pLeaderId;
	}

	public String getLeaderId() {
		return leaderId;
	}

	public int getLv() {
		return lv;
	}

	public void addBattleTimes() {
		this.battleTimes++;
	}

	public int getBattleTimes() {
		return battleTimes;
	}

	public boolean isPersonal() {
		return GCompTeamType.SINGLE_PLAYER == this.teamType;
	}

	@Override
	public String toString() {
		return descr;
	}
}
