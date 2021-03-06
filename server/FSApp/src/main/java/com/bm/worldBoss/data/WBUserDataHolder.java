package com.bm.worldBoss.data;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class WBUserDataHolder {
	private static WBUserDataHolder instance = new WBUserDataHolder();
	
	private static eSynType synType = eSynType.WB_USER_DATA;
	
	public static WBUserDataHolder getInstance(){
		return instance;
	}


	public void syn(Player player, int versionP) {
		int version = player.getDataSynVersionHolder().getVersion(synType);
		if(version != versionP){			
			String userId = player.getUserId();
			WBUserData WBUserData = get(userId);
			if (WBUserData != null) {
				ClientDataSynMgr.synData(player, WBUserData, synType, eSynOpType.UPDATE_SINGLE);
			} else {
				GameLog.error("WBUserDataHolder", "#syn()", "find WBUserData fail:" + userId);
			}
		}
		
	}

	public WBUserData get(String userId) {
		boolean success = true;
		WBUserData data = WBUserDataDao.getInstance().get(userId);
		if(data==null){
			data = WBUserData.newInstance(userId);			
			success = WBUserDataDao.getInstance().update(data);
		}	
		
		return  success? data:null;
	}
	

	public void update(Player player){		
		WBUserData wbUserData = get(player.getUserId());
		update(player, wbUserData);
	}
	
	public void update(Player player, WBUserData wbUserData){		
		WBUserDataDao.getInstance().update(wbUserData);
		
		ClientDataSynMgr.synData(player, wbUserData, synType, eSynOpType.UPDATE_SINGLE);
	}





}
