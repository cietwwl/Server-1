package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

class WBFightEndState implements  IwbState{

	
	final private WBState state = WBState.FightEnd;

	
	@Override
	public IwbState doTransfer() {	
		
		return new WBSendAwardState();
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