package com.bm.worldBoss.state;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

class WBFinishState implements  IwbState{

	final private WBState state = WBState.Finish;
	
	@Override
	public IwbState doTransfer() {		
		
		boolean success = WBMgr.getInstance().tryNextBoss();
		if(success){
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
