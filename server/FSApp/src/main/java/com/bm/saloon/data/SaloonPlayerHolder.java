package com.bm.saloon.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SaloonPlayerHolder{
	
	final private eSynType synType = eSynType.SaloonPlayer;
	
	private static final SaloonPlayerHolder _INSTANCE = new SaloonPlayerHolder();
	
	public static SaloonPlayerHolder getInstance() {
		return _INSTANCE;
	}
	
	public void synAllData(Player player, List<SaloonPlayer> itemList){
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	public void synAddData(Player player, SaloonPlayer addItem){
		ClientDataSynMgr.updateData(player, addItem, synType,eSynOpType.ADD_SINGLE);
	}
	public void synRemoveData(Player player, String removeId){
		SaloonPlayer removeItem = SaloonPlayer.newInstance(removeId);
		ClientDataSynMgr.updateData(player, removeItem, synType,eSynOpType.REMOVE_SINGLE);
	}
//	public void synUpdateData(Player player, SaloonPlayer addItem){
//		ClientDataSynMgr.updateData(player, addItem, synType,eSynOpType.UPDATE_SINGLE);
//	}

	
	
}
