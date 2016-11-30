package com.bm.worldBoss.state;

import javax.management.timer.Timer;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.rw.fsutil.util.DateUtils;

class WBPreStartState implements  IwbState{

	final private WBState state = WBState.PreStart;
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long curTime = System.currentTimeMillis();//1477364091320
//		System.out.println("PreStart state ,start time : " + DateUtils.getDateTimeFormatString(wbData.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		if((wbData.getStartTime() - 5 * Timer.ONE_SECOND) <= curTime){     //提前5s开启,前端会在开启前后10s内进行状态同步
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
