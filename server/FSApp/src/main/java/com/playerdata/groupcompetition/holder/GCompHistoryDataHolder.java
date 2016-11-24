package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompHistoryDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompHistoryData;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 
 * 海选阶段的数据holder
 * 
 * @author CHEN.P
 *
 */
public class GCompHistoryDataHolder {

	private static GCompHistoryDataHolder _instance = new GCompHistoryDataHolder();
	
	public static GCompHistoryDataHolder getInstance() {
		return _instance;
	}
	
	private final GCompHistoryDataDAO _dao;
	
	protected GCompHistoryDataHolder() {
		this._dao = GCompHistoryDataDAO.getInstance();
	}
	
	void loadHistoryData() {
		this._dao.loadHistoryData();
	}
	
	void setSelectedGroupIds(List<String> groupIds) {
		this._dao.get().setSelectedGroupIds(groupIds);
		this._dao.update();
	}
	
	List<String> getSelectedGroupIds() {
		return this._dao.get().getSelectedGroupIds();
	}
	
	void syn(Player player) {
		GCompHistoryData data = _dao.get();
		ClientDataSynMgr.synData(player, data, eSynType.GCompAudition, eSynOpType.UPDATE_SINGLE);
		com.playerdata.groupcompetition.util.GCompUtil.log("同步历史数据，玩家id：{}，数据：{}", player.getUserId(), data);
	}
	
	void addChampion(GCGroup grop) {
		this._dao.get().addChampion(grop);
		this._dao.update();
	}
}
