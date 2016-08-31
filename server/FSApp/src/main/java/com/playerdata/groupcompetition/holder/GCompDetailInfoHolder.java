package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompDetailInfoDAO;
import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompDetailInfoHolder {

	private static final GCompDetailInfoHolder _instance = new GCompDetailInfoHolder();
	
	public static final GCompDetailInfoHolder getInstance() {
		return _instance;
	}
	
	private GCompDetailInfoDAO _dao;
	
	protected GCompDetailInfoHolder() {
		_dao = GCompDetailInfoDAO.getInstance();
	}
	
	public void syn(int matchId, Player player) {
		GCompDetailInfo detailInfo = _dao.getDetailInfo(matchId);
		ClientDataSynMgr.synData(player, detailInfo, eSynType.GCompDetailInfo, eSynOpType.UPDATE_SINGLE);
	}
	
	public void add(GCompDetailInfo detailInfo) {
		_dao.addDetailInfo(detailInfo);
	}
}
