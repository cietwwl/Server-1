package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.dao.GCompEventsDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.holder.data.GCompEventsSynData;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompEventsDataHolder {

	private static final GCompEventsDataHolder _instance = new GCompEventsDataHolder();
	
	public static GCompEventsDataHolder getInstance() {
		return _instance;
	}
	
	private final eSynType _synType = eSynType.GCompMatch;
	private final GCompEventsDataDAO _dao;
	
	protected GCompEventsDataHolder() {
		this._dao = GCompEventsDataDAO.getInstance();
	}
	
	GCompEventsGlobalData get() {
		return _dao.getCurrentGlobalData();
	}
	
	void update() {
		this._dao.update();
	}
	
	void loadEventsGlobalData() {
		this._dao.loadEventsGlobalData();
	}
	
	public void syn(Player toPlayer, GCompEventsSynData synData) {
		ClientDataSynMgr.synData(toPlayer, synData, _synType, eSynOpType.UPDATE_SINGLE);
		com.playerdata.groupcompetition.util.GCompUtil.log("同步数据：{}", synData);
	}
}
