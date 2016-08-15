package com.rw.handler.teamBattle.data;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.teamBattle.service.TeamBattleHandler;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class UserTeamBattleDataHolder {
	private static UserTeamBattleDataHolder instance = new UserTeamBattleDataHolder();
	
	public static UserTeamBattleDataHolder getInstance() {
		return instance;
	}
	
	private UserTeamBattleData utbData = null;
	private String currentHardID = null;
	
	private SynDataListHolder<UserTeamBattleData> listHolder = new SynDataListHolder<UserTeamBattleData>(UserTeamBattleData.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<UserTeamBattleData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			utbData = itemList.get(i);
		}
		for(String id : TeamBattleHandler.HARD_ARR){
			if(!utbData.getFinishedHards().contains(id)){
				currentHardID = id;
				return;
			}	
		}
		currentHardID = null;
	}
	
	public UserTeamBattleData getUserTBData(){
		return utbData;
	}
	
	public String getCurrentHardID(){
		return currentHardID;
	}
}
