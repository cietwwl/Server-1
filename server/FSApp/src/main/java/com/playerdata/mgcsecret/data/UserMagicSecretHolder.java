package com.playerdata.mgcsecret.data;

import java.util.ArrayList;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserMagicSecretHolder {

	private UserMagicSecretDao userMagicSecretDao = UserMagicSecretDao.getInstance();
	private final String userId;
	private static eSynType synType = eSynType.MagicSecretData;

	public UserMagicSecretHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			ClientDataSynMgr.synData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#syn()", "find UserMagicSecretData fail:" + userId);
		}
	}

	public UserMagicSecretData get() {
		UserMagicSecretData umsData = userMagicSecretDao.get(userId);
		if(umsData.getSecretArmy() == null) {
			Player player = PlayerMgr.getInstance().find(userId);
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(userId, player.getHeroMgr().getHeroIdList());
			umsData.setSecretArmy(armyInfo);
			syn(player, 0);
		}
		return umsData;
	}

	public void update(Player player) {
		userMagicSecretDao.update(userId);
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			ClientDataSynMgr.updateData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#update()", "find UserMagicSecretData fail:" + userId);
		}
	}
	
	public void update(Player player, String fieldName){
		userMagicSecretDao.update(userId);
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add(fieldName);
			ClientDataSynMgr.synDataFiled(player, userMagicSecret, synType, list);
		} else {
			GameLog.error("UserMagicSecretHolder", "#updateF()", "find UserMagicSecretData fail:" + userId);
		}
	}

	public void flush() {
	}

}
