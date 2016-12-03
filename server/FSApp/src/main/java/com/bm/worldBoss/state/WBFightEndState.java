package com.bm.worldBoss.state;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

public class WBFightEndState implements  IwbState{

	
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
		if(wbData.getCurLife() > 0 ){
			WBMgr.getInstance().broatCastBossLeave();
		}
		WBMgr.getInstance().broatBossChange(false);
	}

}
