package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

class WBPreStartState implements  IwbState{

	final private WBState state = WBState.PreStart;
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long curTime = System.currentTimeMillis();//1477364091320
//		System.out.println("PreStart state ,start time : " + DateUtils.getDateTimeFormatString(wbData.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		if(wbData.getStartTime() <= curTime){
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
		
		
		
	}
}
