package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

 class WBFightStartState implements  IwbState{

	
	final private WBState state = WBState.FightStart;
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long currentTimeMillis = System.currentTimeMillis();
		if(wbData.getEndTime() > currentTimeMillis || wbData.getCurLife() <= 0){
			return new WBFightEndState();
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
