package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;

public class GCompBaseInfoMgr {

	private static final GCompBaseInfoMgr _instance = new GCompBaseInfoMgr();
	
	public static final GCompBaseInfoMgr getInstance() {
		return _instance;
	}
	
	private final GCompBaseInfoHolder _dataHolder = GCompBaseInfoHolder.getInstance();
	
	protected GCompBaseInfoMgr() {
		
	}
	
	public void sendBaseInfo(Player player) {
		this._dataHolder.syn(player);
	}
}
