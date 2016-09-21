package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;

public class GCompSelectionDataMgr {

	private static final GCompSelectionDataMgr _instance = new GCompSelectionDataMgr();
	
	public static GCompSelectionDataMgr getInstance() {
		return _instance;
	}
	
	private GCompSelectionDataHolder _dataHolder;
	protected GCompSelectionDataMgr() {
		_dataHolder = GCompSelectionDataHolder.getInstance();
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
