package com.rwbase.dao.user;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGameDataHolder {

	private UserGameDataDao userGameDataDao = UserGameDataDao.getInstance();
	private final String userId;
	private static eSynType synType = eSynType.USER_GAME_DATA;

	public UserGameDataHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		UserGameData userGameData = get();
		if (userGameData != null) {
			ClientDataSynMgr.synData(player, userGameData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGameDataHolder", "#syn()", "find UserGameData fail:" + userId);
		}
	}

	public UserGameData get() {
		return userGameDataDao.get(userId);
	}

	public void update(Player player) {
		userGameDataDao.update(userId);
		UserGameData userGameData = get();
		if (userGameData != null) {
			ClientDataSynMgr.updateData(player, userGameData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGameDataHolder", "#update()", "find UserGameData fail:" + userId);
		}
	}

	public void flush() {
	}

}
