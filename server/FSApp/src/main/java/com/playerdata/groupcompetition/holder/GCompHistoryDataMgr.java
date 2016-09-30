package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;

public class GCompHistoryDataMgr {

	private static final GCompHistoryDataMgr _instance = new GCompHistoryDataMgr();
	
	public static GCompHistoryDataMgr getInstance() {
		return _instance;
	}
	
	private GCompHistoryDataHolder _dataHolder;
	protected GCompHistoryDataMgr() {
		_dataHolder = GCompHistoryDataHolder.getInstance();
	}
	
	public void setSelectedGroupIds(List<String> groupIds) {
		this._dataHolder.setSelectedGroupIds(groupIds);
	} 
	
	public List<String> getSelectedGroupIds() {
		return this._dataHolder.getSelectedGroupIds();
	}
	
	public void sendLastMatchData(Player player) {
		this._dataHolder.syn(player);
	}
}
