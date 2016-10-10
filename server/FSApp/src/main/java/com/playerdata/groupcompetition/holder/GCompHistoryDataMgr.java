package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.dao.GCompHistoryDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompHistoryData;

public class GCompHistoryDataMgr {

	private static final GCompHistoryDataMgr _instance = new GCompHistoryDataMgr();
	
	public static GCompHistoryDataMgr getInstance() {
		return _instance;
	}
	
	private GCompHistoryDataHolder _dataHolder;
	protected GCompHistoryDataMgr() {
		_dataHolder = GCompHistoryDataHolder.getInstance();
	}
	
	public void serverStartComplete() {
		this._dataHolder.loadHistoryData();
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
	
	public GCompHistoryData getHistoryData() {
		return GCompHistoryDataDAO.getInstance().get();
	}
}
