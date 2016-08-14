package com.rwbase.dao.user;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserDataHolder {// 战斗数据

	//private User user;
	private UserDataDao userDataDao = UserDataDao.getInstance();
	private final String userId;
	private static eSynType synType = eSynType.USER_DATA;

	public UserDataHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		User user = get();
		if (user != null) {
			ClientDataSynMgr.synData(player, user, synType, eSynOpType.UPDATE_SINGLE);
		}

	}

	public User get() {
		return userDataDao.getByUserId(userId);
	}

	public void update(Player player) {
		userDataDao.update(userId);
		User user = get();
		if (user != null) {
			ClientDataSynMgr.updateData(player, user, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserDataHolder", "UserDataHolder#update()", "find user fail:" + userId);
		}
	}
	
	public boolean updateDBDirect(Player player) {
		User user = get();
		boolean updateToDB = userDataDao.updateToDB(user);
		if (updateToDB) {
			if (user != null) {
				ClientDataSynMgr.updateData(player, user, synType, eSynOpType.UPDATE_SINGLE);
			} else {
				GameLog.error("UserDataHolder", "UserDataHolder#update()", "find user fail:" + userId);
			}
		}
		return updateToDB;
	}

	public void flush() {
	}

}
