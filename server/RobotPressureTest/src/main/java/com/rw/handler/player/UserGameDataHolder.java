package com.rw.handler.player;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class UserGameDataHolder {
	private SynDataListHolder<UserGameData> listHolder = new SynDataListHolder<UserGameData>(UserGameData.class);
	
	private UserGameData userGameData;
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<UserGameData> itemList = listHolder.getItemList();
		userGameData = itemList.get(0);
	}

	public UserGameData getUserGameData() {
		return userGameData;
	}
}
