package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompEventsDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompEventsSynData;
import com.playerdata.groupcompetition.util.GCompUtil;
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
	
	GCompEventsSynData get() {
		return _dao.get();
	}
	
	public void syn(Player toPlayer) {
		GCompEventsSynData synData = this.get();
		ClientDataSynMgr.synData(toPlayer, synData, _synType, eSynOpType.UPDATE_SINGLE);
		GCompUtil.log("同步数据：{}", synData);
	}
	
	public void synToAll() {
		SynToAllTask.createNewTaskAndSubmit(this.get(), _synType, eSynOpType.UPDATE_SINGLE);
	}
}
