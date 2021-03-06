package com.bm.worldBoss.state;

import com.bm.worldBoss.WBAwardServer;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

public class WBSendAwardState implements  IwbState{
	
	final private WBState state = WBState.SendAward;
	
	@Override
	public IwbState doTransfer() {
		boolean isSendAwardFinish = !WBAwardServer.getInstance().isRunning();
		long curTime = System.currentTimeMillis();
		WBData wbData = WBDataHolder.getInstance().get();
		if(isSendAwardFinish && wbData.getFinishTime() <= curTime){			
			return new WBFinishState();	
		}
		return null;
	}

	@Override
	public WBState getState() {
		return WBState.SendAward;
	}

	@Override
	public void doEnter() {
		WBData wbData = WBDataHolder.getInstance().get();
		wbData.setState(state);
		WBDataHolder.getInstance().update();	
		
		WBAwardServer.getInstance().doAwardTask();

	}
}
