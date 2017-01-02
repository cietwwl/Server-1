package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompTeamDataDAO;
import com.playerdata.groupcompetition.dao.pojo.GCompGroupTeamMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompTeamHolder {

	private static GCompTeamHolder _instance = new GCompTeamHolder();

	public static GCompTeamHolder getInstance() {
		return _instance;
	}

	private static final eSynType synType = eSynType.GCompTeamHolder;
	private static final eSynOpType synOpType = eSynOpType.UPDATE_SINGLE;

	private GCompTeamDataDAO _dao;

	protected GCompTeamHolder() {
		this._dao = GCompTeamDataDAO.getInstance();
	}

	void addTeam(Player player, GCEventsType eventsType, int matchId, String groupId, GCompTeam data) {
		_dao.addTeam(eventsType, matchId, groupId, data);
		ClientDataSynMgr.synData(player, data, synType, synOpType);
	}

	/**
	 * 
	 * 根据比赛id，角色id，帮派id去获取一个玩家在帮派争霸中的队伍
	 * 
	 * @param matchId
	 * @param userId
	 * @param groupId
	 * @return
	 */
	GCompTeam getTeamOfUser(int matchId, String userId, String groupId) {
		// 获取user所属的队伍
		return _dao.getTeamOfUser(matchId, userId, groupId);
	}

	/**
	 * 
	 * @param matchId
	 * @param teamId
	 * @return
	 */
	GCompTeam getTeamByTeamId(int matchId, String teamId) {
		return _dao.getTeamDataByTeamId(matchId, teamId);
	}

	void clearTeamData() {
		_dao.clearMatchTeamData();
	}

	List<GCompTeam> clearAllTeam() {
		return _dao.removeAllTeam();
	}

	void removeTeam(int matchId, String groupId, GCompTeam team) {
		_dao.removeTeamData(matchId, groupId, team);
		// GCompUtil.log("移除队伍：{}，matchId：{}，team：{}", team.getTeamId(), matchId, team);
	}

	void createTeamData(List<? extends IGCAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			IGCAgainst against = againsts.get(i);
			GCompGroupTeamMgr holder = new GCompGroupTeamMgr(against.getGroupA().getGroupId(), against.getGroupB().getGroupId());
			_dao.addGroupTeamData(against.getId(), holder);
		}
	}

	public void syn(int matchId, Player player) {
		String groupId = GroupHelper.getInstance().getGroupId(player);
		if (groupId != null && groupId.length() > 0) {
			GCompTeam synData = this.getTeamOfUser(matchId, player.getUserId(), groupId);
			if (synData != null) {
				ClientDataSynMgr.synData(player, synData, synType, synOpType);
			}
		}
	}

	public void synToAllMembers(GCompTeam data) {
		List<GCompTeamMember> members = data.getMembers();
		if (members.size() == 1) {
			ClientDataSynMgr.synData(PlayerMgr.getInstance().find(members.get(0).getUserId()), data, synType, synOpType);
		} else {
			List<Player> players = new ArrayList<Player>();
			for (GCompTeamMember member : members) {
				players.add(PlayerMgr.getInstance().find(member.getUserId()));
			}
			ClientDataSynMgr.synDataMutiple(players, data, synType, synOpType);
		}
	}

	public void update(Player player, GCompTeam data) {
		ClientDataSynMgr.synData(player, data, synType, synOpType);
	}

	public void synRemove(Player player, GCompTeam data) {
		ClientDataSynMgr.synData(player, data, synType, eSynOpType.REMOVE_SINGLE);
	}
}
