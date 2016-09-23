package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompTeamDataDAO;
import com.playerdata.groupcompetition.dao.pojo.GCompGroupTeamMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompTeamHolder {

	private static final GCompTeamHolder _instance = new GCompTeamHolder();
	
	public static final GCompTeamHolder getInstance() {
		return _instance;
	}
	
	private GCompTeamDataDAO _dao;
	
	protected GCompTeamHolder() {
		this._dao = GCompTeamDataDAO.getInstance();
	}
	
	void addTeam(Player player, GCEventsType eventsType, int matchId, String groupId, GCompTeam data)  {
		_dao.addTeam(eventsType, matchId, groupId, data);
		ClientDataSynMgr.synData(player, data, eSynType.GCompTeamHolder, eSynOpType.UPDATE_SINGLE);
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

	void createTeamData(List<? extends IGCAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			IGCAgainst against = againsts.get(i);
			GCompGroupTeamMgr holder = new GCompGroupTeamMgr(against.getGroupA().getGroupId(), against.getGroupB().getGroupId());
			_dao.addGroupTeamData(against.getId(), holder);
		}
	}
	
	public void syn(int matchId, Player player) {
		String groupId = GroupHelper.getGroupId(player);
		if (groupId != null && groupId.length() > 0) {
			GCompTeam synData = this.getTeamOfUser(matchId, player.getUserId(), groupId);
			if (synData != null) {
				ClientDataSynMgr.synData(player, synData, eSynType.GCompTeamHolder, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
	
	public void synToAll(GCompTeam data, List<Player> players) {
		ClientDataSynMgr.synDataMutiple(players, data, eSynType.GCompTeamHolder, eSynOpType.UPDATE_SINGLE);
	}
}
