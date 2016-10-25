package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.rank.WBHurtRankMgr;

class WBPreStartState implements  IwbState{

	final private WBState state = WBState.PreStart;
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long curTime = System.currentTimeMillis();//1477364091320
		if(wbData.getStartTime() <= curTime){     //1477367100000
			return new WBFightStartState();
		}
		
		return 	null;	
	}

	@Override
	public WBState getState() {
		return state;
	}

	@Override
	public void doEnter() {
		WBData wbData = WBDataHolder.getInstance().get();
		wbData.setState(state);
		WBDataHolder.getInstance().update();
		
		WBHurtRankMgr.clearRank();	
		
	}
}
