package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompBaseInfoDAO;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompBaseInfoHolder {
	
	private static final GCompBaseInfoHolder _instance = new GCompBaseInfoHolder();
	
	public static final GCompBaseInfoHolder getInstance() {
		return _instance;
	}
	
	private eSynType _synType = eSynType.GCompBase;
	private GCompBaseInfoDAO _dao;
	protected GCompBaseInfoHolder() {
		this._dao = GCompBaseInfoDAO.getInstance();
	}
	
	public void syn(Player player) {
		ClientDataSynMgr.synData(player, _dao.getBaseInfo(), _synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synToAll() {
		SynToAllTask.createNewTaskAndSubmit(_dao.getBaseInfo(), _synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void update(long startTime) {
		this._dao.updateStartTime(startTime);
	}
	
	public void update(boolean start) {
		this._dao.update(start);
	}

	public void update(GCompStageType currentStage) {
		this._dao.updateStage(currentStage);
	}
}
