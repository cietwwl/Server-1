package com.playerdata.groupcompetition.holder;

import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;

public class GCompDetailInfoMgr {

	private static final GCompDetailInfoMgr _instance = new GCompDetailInfoMgr();
	
	public static GCompDetailInfoMgr getInstance() {
		return _instance;
	}
	
	private GCompDetailInfoHolder _dataHolder;

	protected GCompDetailInfoMgr() {
		_dataHolder = new GCompDetailInfoHolder();
	}

	public void onEventsStart(int matchId, String idOfGroupA, String idOfGroupB) {
		GCompDetailInfo detailInfo = GCompDetailInfo.createNew(matchId, idOfGroupA, idOfGroupB);
		_dataHolder.add(detailInfo);
	}
}
