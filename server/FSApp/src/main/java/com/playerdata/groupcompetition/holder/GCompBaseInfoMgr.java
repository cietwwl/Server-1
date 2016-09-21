package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.groupcompetition.util.GCompStageType;

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
	
	public void update(long startTime) {
		this._dataHolder.update(startTime);
	}
	
	public void update(boolean start) {
		this._dataHolder.update(start);
	}
	
	public void update(GCompStageType currentStage) {
		this._dataHolder.update(currentStage);
	}
	
	public GCompStageType getCurrentStageType() {
		return this._dataHolder.getCurrentStageType();
	}
}
