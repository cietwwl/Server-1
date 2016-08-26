package com.playerdata.groupcompetition.holder;

import java.util.List;

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
}
