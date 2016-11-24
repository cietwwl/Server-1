package com.bm.worldBoss.data;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class WBBroatCastDataHolder {
	private static WBBroatCastDataHolder instance = new WBBroatCastDataHolder();
	
	private static eSynType synType = eSynType.WB_Broatcast;

	
	public static WBBroatCastDataHolder getInstance(){
		return instance;
	}

	public void syn(Player player, WBBroatCastData broatCastData) {
		
		ClientDataSynMgr.synData(player, broatCastData, synType, eSynOpType.UPDATE_SINGLE, -1);
		
	}






}
