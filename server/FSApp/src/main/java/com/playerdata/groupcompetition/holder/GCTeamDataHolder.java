package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompTeamDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompTeamSynData;
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
	
	private GCompTeamSynData get(int matchId, String userId) {
		// 获取user所属的队伍
		return _dao.getTeamData(matchId, userId);
	}
	
	void clearTeamData() {
		_dao.clearMatchTeamData();
	}
	
	public void syn(int matchId, Player player) {
		GCompTeamSynData synData = this.get(matchId, player.getUserId());
		if (synData != null) {
			ClientDataSynMgr.synData(player, synData, eSynType.GCompTeamHolder, eSynOpType.UPDATE_SINGLE);
		}
	}
}
