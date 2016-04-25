package com.playerdata.charge.dao;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ChargeInfoHolder {
	
	private static eSynType synType = eSynType.Charge;
	
	private static ChargeInfoHolder instance = new ChargeInfoHolder();
	
	public static ChargeInfoHolder getInstance(){
		return instance;
	}

	public void syn(Player player,int version) {
		String userId = player.getUserId();
		ChargeInfo chargeInfo = get(userId);	
		if (chargeInfo != null) {
			ClientDataSynMgr.synData(player, chargeInfo, synType, eSynOpType.UPDATE_SINGLE);
		} else {			
			GameLog.error(LogModule.Charge, "ChargeInfoHolder[newChargeInfo]", "chargeInfo is null. userId:" + userId,null);
		}
	}

	private ChargeInfo newChargeInfo(String userId){
		ChargeInfo chargeInfo = new ChargeInfo();
		chargeInfo.setUserId(userId);
		if(ChargeInfoDao.getInstance().update(chargeInfo)){
			GameLog.info(LogModule.Charge.getName(), "ChargeInfoHolder[newChargeInfo]", "success userId:" + userId,null);
			return chargeInfo;
		}else{
			GameLog.error(LogModule.Charge, "ChargeInfoHolder[newChargeInfo]", "db save fail. userId:" + userId,null);
		}
		return null;
	}

	public ChargeInfo get(String userId) {		
		ChargeInfo chargeInfo = ChargeInfoDao.getInstance().get(userId);
		if(chargeInfo == null){
			chargeInfo = newChargeInfo(userId);
		}
		return chargeInfo;
	}

	public void update(Player player) {
		
		String userId = player.getUserId();
		ChargeInfo ChargeInfo = get(userId);
		if (ChargeInfo != null && ChargeInfoDao.getInstance().update(ChargeInfo)) {
			ClientDataSynMgr.updateData(player, ChargeInfo, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("ChargeInfoHolder", "#update()", "find ChargeInfo fail:" + userId);
		}
	}


}
