package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.dao.GCompHistoryDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompHistoryData;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.dao.group.pojo.Group;

public class GCompHistoryDataMgr {

	private static GCompHistoryDataMgr _instance = new GCompHistoryDataMgr();
	
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
	
	public List<GCompAgainst> getAllAgainsts() {
		return new ArrayList<GCompAgainst>(GCompHistoryDataDAO.getInstance().get().getAgainsts());
	}
	
	public void notifyGroupInfoChange(Group group) {
		GCompHistoryDataDAO dao = GCompHistoryDataDAO.getInstance();
		List<GCompAgainst> list =  dao.get().getAgainsts();
		GCompUtil.updateGroupInfo(list, group);
		dao.update();
	}
}
