package com.playerdata.teambattle.data;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserTeamBattleDataHolder {
	private static UserTeamBattleDataHolder instance = new UserTeamBattleDataHolder();
	
	public static UserTeamBattleDataHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.GFightOnlinePersonalData;
	
	public UserTeamBattleData get(String userID) {
		return UserTeamBattleDAO.getInstance().get(userID);
	}
	
	public void update(Player player, UserTeamBattleData data) {
		UserTeamBattleDAO.getInstance().update(data);
		ClientDataSynMgr.synData(player, data, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 同步数据
	 * @param player
	 */
	public void synData(Player player) {
		UserTeamBattleData userTBData = get(player.getUserId());
		if (userTBData != null) {
			ClientDataSynMgr.synData(player, userTBData, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
}
