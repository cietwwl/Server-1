package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompSelectionDataDAO;
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
public class GCompSelectionDataHolder {

	private static final GCompSelectionDataHolder _instance = new GCompSelectionDataHolder();
	
	public static final GCompSelectionDataHolder getInstance() {
		return _instance;
	}
	
	private final List<String> _selectedGroupIds = new ArrayList<String>();
	
	private final GCompSelectionDataDAO _dao;
	
	protected GCompSelectionDataHolder() {
		this._dao = GCompSelectionDataDAO.getInstance();
	}
	
	void setSelectedGroupIds(List<String> groupIds) {
		this._selectedGroupIds.clear();
		this._selectedGroupIds.addAll(groupIds);
	}
	
	List<String> getSelectedGroupIds() {
		return Collections.unmodifiableList(_selectedGroupIds);
	}
	
	void syn(Player player) {
		ClientDataSynMgr.synData(player, _dao.get(), eSynType.GCompAudition, eSynOpType.UPDATE_SINGLE);
	}
	
	void addChampion(GCGroup grop) {
		this._dao.get().addChampion(grop);
		this._dao.update();
	}
}
