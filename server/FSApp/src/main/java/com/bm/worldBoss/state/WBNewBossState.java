package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

class WBNewBossState implements  IwbState{

	final private WBState state = WBState.NewBoss;	
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long curTime = System.currentTimeMillis();
		if(wbData.getPreStartTime() > curTime){
			return new WBPreStartState();
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
	}
	
}
