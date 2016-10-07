package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompDetailInfoDAO;
import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.playerdata.groupcompetition.util.GCompUtil;
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
	
	public boolean syn(int matchId, Player player) {
		GCompDetailInfo detailInfo = _dao.getDetailInfo(matchId);
		if (detailInfo != null) {
			ClientDataSynMgr.synData(player, detailInfo, eSynType.GCompDetailInfo, eSynOpType.UPDATE_SINGLE);
//			GCompUtil.log("---------- syn:同步DetailInfo数据：{} ----------", detailInfo);
			return true;
		} else {
			GCompUtil.log("---------- syn:请求同步数据：{}， 不存在指定matchId的数据 ----------", detailInfo);
			return false;
		}
	}
	
	public boolean synPlayers(int matchId, List<Player> players) {
		GCompDetailInfo detailInfo = _dao.getDetailInfo(matchId);
		if (detailInfo != null) {
			ClientDataSynMgr.synDataMutiple(players, detailInfo, eSynType.GCompDetailInfo, eSynOpType.UPDATE_SINGLE);
//			GCompUtil.log("---------- synPlayers:同步DetailInfo数据：{} ----------", detailInfo);
			return true;
		} else {
			GCompUtil.log("---------- synPlayers:请求同步数据：{}， 不存在指定matchId的数据 ----------", detailInfo);
			return false;
		}
	}
	
	public void add(GCompDetailInfo detailInfo) {
		_dao.addDetailInfo(detailInfo);
	}
	
	void update() {
		_dao.update();
	}
	
	void reset() {
		_dao.reset();
	}
	
	void loadData() {
		_dao.loadDetailInfo();
	}
	
	public GCompDetailInfo get(int matchId) {
		return _dao.getDetailInfo(matchId);
	}
}
