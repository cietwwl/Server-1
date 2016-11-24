package com.bm.saloon.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SaloonPositionHolder{
	
	final private eSynType synType = eSynType.SaloonPosition;
	
	private static final SaloonPositionHolder _INSTANCE = new SaloonPositionHolder();
	
	public static SaloonPositionHolder getInstance() {
		return _INSTANCE;
	}
	
	public void synAllData(Player player, List<SaloonPosition> itemList){
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	public void synAddData(Player player, SaloonPosition addItem){
		ClientDataSynMgr.updateData(player, addItem, synType,eSynOpType.ADD_SINGLE);
	}
	public void synRemoveData(Player player, SaloonPosition addItem){
		ClientDataSynMgr.updateData(player, addItem, synType,eSynOpType.REMOVE_SINGLE);
	}
	public void synUpdateData(Player player, SaloonPosition addItem){
		ClientDataSynMgr.updateData(player, addItem, synType,eSynOpType.UPDATE_SINGLE);
	}

	
	
}
