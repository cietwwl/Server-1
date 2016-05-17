package com.bm.groupSecret.data.user;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGroupSecretDataHolder {

	private static UserGroupSecretDataHolder instance = new UserGroupSecretDataHolder();
	
	public static UserGroupSecretDataHolder getInstance(){
		return instance;
	}
	private static eSynType synType = eSynType.USER_GAME_DATA;

	public void syn(Player player, int version) {
		String userId = player.getUserId();
		UserGroupSecretData UserGroupSecretData = get(player);
		if (UserGroupSecretData != null) {
			ClientDataSynMgr.synData(player, UserGroupSecretData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGroupSecretDataHolder", "#syn()", "find UserGroupSecretData fail:" + userId);
		}
	}

	public UserGroupSecretData get(Player player) {
		return UserGroupSecretDataDAO.getInstance().get(player.getUserId());
	}

	public void update(Player player) {
		String userId = player.getUserId();
		UserGroupSecretDataDAO.getInstance().update(userId);
		UserGroupSecretData UserGroupSecretData = get(player);
		if (UserGroupSecretData != null) {
			ClientDataSynMgr.updateData(player, UserGroupSecretData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGroupSecretDataHolder", "#update()", "find UserGroupSecretData fail:" + userId);
		}
	}
	


}
