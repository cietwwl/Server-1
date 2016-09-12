package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

class WBNewBossState implements  IwbState{

	final private WBState state = WBState.NewBoss;
	
	final long PREPARE_TIME = 5*60*1000; //备战5分钟
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long currentTimeMillis = System.currentTimeMillis();
		if(wbData.getStartTime() < currentTimeMillis + PREPARE_TIME){
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
