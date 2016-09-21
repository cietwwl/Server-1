package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompMatchDataDAO;
import com.playerdata.groupcompetition.holder.data.GCompMatchSynData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompMatchDataHolder {

	private static final GCompMatchDataHolder _instance = new GCompMatchDataHolder();
	
	public static GCompMatchDataHolder getInstance() {
		return _instance;
	}
	
	private final eSynType _synType = eSynType.GCompMatch;
	private final GCompMatchDataDAO _dao;
	
	protected GCompMatchDataHolder() {
		this._dao = GCompMatchDataDAO.getInstance();
	}
	
	GCompMatchSynData get() {
		return _dao.get();
	}
	
	public void syn(Player toPlayer) {
		GCompMatchSynData synData = this.get();
		ClientDataSynMgr.synData(toPlayer, synData, _synType, eSynOpType.UPDATE_SINGLE);
		System.err.println("同步数据：" + synData);
	}
	
	public void synToAll() {
		SynToAllTask.createNewTaskAndSubmit(this.get(), _synType, eSynOpType.UPDATE_SINGLE);
	}
}
