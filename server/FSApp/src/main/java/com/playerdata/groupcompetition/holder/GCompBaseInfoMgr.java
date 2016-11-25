package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;

public class GCompBaseInfoMgr {

	private static GCompBaseInfoMgr _instance = new GCompBaseInfoMgr();
	
	public static GCompBaseInfoMgr getInstance() {
		return _instance;
	}
	
	private final GCompBaseInfoHolder _dataHolder = GCompBaseInfoHolder.getInstance();
	
	protected GCompBaseInfoMgr() {
		
	}
	
	private GCompBaseInfo createBaseInfo() {
		return GroupCompetitionMgr.getInstance().createBaseInfoSynData();
	}
	
	public void sendBaseInfo(Player player) {
		this._dataHolder.syn(player, createBaseInfo());
	}
	
	public void sendBaseInfoToAll() {
		this._dataHolder.synToAll(createBaseInfo());
	}
}
