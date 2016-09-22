package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompTeamDataDAO;
import com.playerdata.groupcompetition.dao.pojo.GCompGroupTeamHolder;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCTeamDataHolder {

	private static final GCTeamDataHolder _instance = new GCTeamDataHolder();
	
	public static final GCTeamDataHolder getInstance() {
		return _instance;
	}
	
	private GCompTeamDataDAO _dao;
	
	protected GCTeamDataHolder() {
		this._dao = GCompTeamDataDAO.getInstance();
	}
	
	private GCompTeam get(int matchId, String userId) {
		// 获取user所属的队伍
		return _dao.getTeamData(matchId, userId);
	}
	
	void clearTeamData() {
		_dao.clearMatchTeamData();
	}

	void createTeamData(List<? extends IGCAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			IGCAgainst against = againsts.get(i);
			GCompGroupTeamHolder holder = new GCompGroupTeamHolder(against.getGroupA().getGroupId(), against.getGroupB().getGroupId());
			_dao.addGroupTeamData(against.getId(), holder);
		}
	}
	
	public void syn(int matchId, Player player) {
		GCompTeam synData = this.get(matchId, player.getUserId());
		if (synData != null) {
			ClientDataSynMgr.synData(player, synData, eSynType.GCompTeamHolder, eSynOpType.UPDATE_SINGLE);
		}
	}
}
